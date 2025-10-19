package script.Server;

import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import logging.LoggingManager;
import logging.entry.ConsumeLog;
import logging.entry.EnchantLog;
import network.center.Center;
import network.center.praise.PraiseDonationMeso;
import network.center.praise.PraiseDonationMesoLog;
import network.center.praise.PraiseDonationMesoRank;
import network.center.praise.PraiseDonationMesoRankEntry;
import network.models.CField;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.*;
import objects.users.PraisePoint;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.ItemFlag;
import objects.users.extra.*;
import objects.users.stone.ImprintedStoneOption;
import objects.users.stone.ImprintedStonePayType;
import objects.utils.Randomizer;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import constants.GameConstants;
import constants.QuestExConstants;

public class  JinCustomNPC extends ScriptEngineNPC {

    String[] grades = new String[] {
            "Unrank", "C", "B", "A", "S", "SS", "SSS"
    };

    ItemEntry[] itemList = new ItemEntry[] {
            new ItemEntry(5680157, new int[]{3500, 3200, 3200, 2800, 2500, 2000}, new int[]{1, 1, 2, 3, 3, 3}, 1, 1),
            new ItemEntry(5069001, new int[]{2200, 2000, 2000, 1700, 1500, 1300}, new int[]{1, 1, 2, 3, 3, 3}, 2, 1),
            new ItemEntry(5060048, new int[]{3500, 3200, 3200, 2800 ,2500, 2000}, new int[]{1, 1, 2, 3, 3, 3}, 3, 1),
            new ItemEntry(5068300, new int[]{4500, 4000, 4000, 3500, 3000, 2500}, new int[]{1, 1, 2, 3, 3, 3}, 4, 1),
            new ItemEntry(5069100, new int[]{2200, 2000, 2000, 1700, 1500, 1300}, new int[]{1, 1, 2, 3, 3, 3}, 5, 1),
            new ItemEntry(5121060, new int[]{4500, 4000, 4000, 3500, 3000, 2500}, new int[]{1, 1, 1, 1, 1, 1}, 6, 1),
            new ItemEntry(2049360, new int[]{0, 6500, 6500, 5500, 5000, 4000}, new int[]{0, 1, 1, 2, 3, 3}, 7, 2),
            new ItemEntry(2434557, new int[]{0, 0, 8000, 7000, 6000, 5000}, new int[]{0, 0, 1, 2, 3, 3}, 8, 3),
            new ItemEntry(5121060, new int[]{0, 0, 4000, 3500, 3000, 2500}, new int[]{0, 0, 1, 1, 1, 1}, 9, 3),
            new ItemEntry(2049360, new int[]{0, 0, 6500, 5500, 5000, 4000}, new int[]{0, 0, 1, 2, 3, 3}, 10, 3),
            new ItemEntry(2439660, new int[]{0, 0, 0, 0, 12000, 10000}, new int[]{0, 0, 0, 0, 1, 2}, 11, 5),
            new ItemEntry(5680409, new int[]{0, 0, 0, 0, 0, 5000}, new int[]{0, 0, 0, 0, 0, 1}, 12, 6),
            new ItemEntry(5680410, new int[]{0, 0, 0, 0, 0, 10000}, new int[]{0, 0, 0, 0, 0, 1}, 13, 6),
            new ItemEntry(5680411, new int[]{0, 0, 0, 0, 0, 20000}, new int[]{0, 0, 0, 0, 0, 1}, 14, 6),
    };
    public void black_bean_membership() {
        if (DBConfig.isGanglim) {
            return;
        }
        initNPC(MapleLifeFactory.getNPC(9062153));

        displayShop();
    }

    public void displayShop() {
        int grade = getPlayer().getOneInfoQuestInteger(100161, "lv");
        String gradeName = "#e<" + grades[grade] + "등급 멤버십 상점>#n";
        String v0 = gradeName + "\r\n보급 상자는 매일 1회 무료로 지원됩니다.";
        v0 += "\r\n#e보유 포인트 : " + NumberFormat.getInstance().format(getPlayer().getRealCash()) + "#n\r\n";
        int item = 2630688 + grade;

        if (grade > 0) {
            if (getPlayer().getOneInfoQuestInteger(100161, "0_buy_count") == 0) {
                v0 += "#L0##b#e#i" + item + "# #z" + item + "##n 보급 받기#k#l\r\n";
            } else {
                v0 += "\r\n#b#e#i" + item + "# #z" + item + "##n 보급 받기 #e#k(수령 완료)#n#l";
            }
        }
        v0 += "\r\n\r\n#e<데일리 멤버쉽 상점>#n\r\n#b";

        if (grade == 0) {
            grade = 1;
        }
        for (ItemEntry entry : itemList) {
            int itemID = entry.getItemID();
            int index = entry.getIndex();
            int buyCount = getPlayer().getOneInfoQuestInteger(100161, index + "_buy_count");
            int remain = entry.getWorldLimit(grade - 1) - buyCount; // 금일 구매 가능 횟수
            int gradeLimit = entry.getGradeLimit();
            int price = entry.getPrice(grade - 1);
            if (gradeLimit > getPlayer().getOneInfoQuestInteger(100161, "lv")) {
                // 구매 가능 등급이 아닐 때
                v0 += "     #i" + itemID + "#  #z" + itemID + "# (" + NumberFormat.getInstance().format(price) + " P)#l\r\n";
                v0 += "#k#e           - 금일 구매 가능 : 0회#b#n #r(등급 부족)#b\r\n";
            } else {
                if (remain <= 0) {
                    // 구매 가능 횟수가 없을 때
                    v0 += "     #i" + itemID + "#  #z" + itemID + "# (" + NumberFormat.getInstance().format(price) + " P)#l\r\n";
                    v0 += "#k#e           - 금일 구매 가능 : " + remain + "회#b#n\r\n";
                } else {
                    v0 += "#L" + index + "##i" + itemID + "#  #z" + itemID + "# (" + NumberFormat.getInstance().format(price) + " P)#l\r\n";
                    v0 += "#k#e           - 금일 구매 가능 : " + remain + "회#b#n\r\n";
                }
            }
        }
        int v1 = self.askMenu(v0);
        if (v1 == 0) {
            if (getPlayer().getOneInfoQuestInteger(100161, "0_buy_count") != 0) {
                return;
            }
            getPlayer().updateOneInfo(100161, "0_buy_count", "1");
            if (1 == target.exchange(item, 1)) {
                if (0 == self.askMenu("#b#i" + item + "# #z" + item + "##k을 보급해드렸습니다.\r\n\r\n#b#L0#아이템 목록으로 돌아간다.#l")) {
                    displayShop();
                }
            } else {
                getPlayer().updateOneInfo(100161, "0_buy_count", "0");
                self.say("인벤토리 슬롯을 확보하고 다시 시도해주시기 바랍니다.");
            }
        } else {
            if (v1 == -1) {
                return;
            }
            if (v1 > itemList.length) {
                return;
            }
            ItemEntry pick = itemList[v1 - 1];
            if (pick == null) {
                return;
            }
            if (pick.getGradeLimit() > grade) {
                return;
            }
            int buyCount = getPlayer().getOneInfoQuestInteger(100161, pick.getIndex() + "_buy_count");
            int remain = pick.getWorldLimit(grade - 1) - buyCount;
            if (0 >= remain) {
                return;
            }
            String v2 = "#e<데일리 멤버쉽 상점>#n\r\n#b#i" + pick.getItemID() + "# #z" + pick.getItemID() + "##k";
            v2 += "\r\n\r\n#e금일 구매 가능 횟수 : " + remain + "회\r\n보유 포인트 : " + NumberFormat.getInstance().format(getPlayer().getRealCash());
            v2 += "\r\n\r\n#n구매 시 #b#e" + pick.getPrice(grade - 1) + " 포인트#n#k가 차감됩니다. 정말 구매하시겠습니까?";
            if (1 == self.askYesNo(v2)) {
                if (getPlayer().getRealCash() < pick.getPrice(grade - 1)) {
                    self.say("강림 포인트가 부족하여 구매할 수 없습니다.");
                    return;
                }
                if (1 == target.exchange(pick.getItemID(), 1)) {
                    getPlayer().gainRealCash(-pick.getPrice(grade - 1));
                    // 구매 처리
                    getPlayer().updateOneInfo(100161, pick.getIndex() + "_buy_count", String.valueOf(buyCount + 1));
                    if (0 == self.askMenu("#b#i" + pick.getItemID() + "# #z" + pick.getItemID() + "##k 구매가 완료되었습니다.\r\n\r\n#b#L0#아이템 목록으로 돌아간다.#l")) {
                        displayShop();
                    }
                } else {
                    self.say("인벤토리 슬롯을 확보하고 다시 시도해주시기 바랍니다.");
                }
            }
        }
    }

    public class ItemEntry {
        private int itemID;
        private int[] price;
        private int[] worldLimit;
        private int index;
        private int gradeLimit;

        public ItemEntry(int itemID, int[] price, int[] worldLimit, int index, int gradeLimit) {
            this.setItemID(itemID);
            this.setPrice(price);
            this.setWorldLimit(worldLimit);
            this.setIndex(index);
            this.setGradeLimit(gradeLimit);
        }

        public int getItemID() {
            return itemID;
        }

        public void setItemID(int itemID) {
            this.itemID = itemID;
        }

        public int getPrice(int index) {
            return price[index];
        }

        public void setPrice(int[] price) {
            this.price = price;
        }

        public int getWorldLimit(int index) {
            return worldLimit[index];
        }

        public void setWorldLimit(int[] worldLimit) {
            this.worldLimit = worldLimit;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getGradeLimit() {
            return gradeLimit;
        }

        public void setGradeLimit(int gradeLimit) {
            this.gradeLimit = gradeLimit;
        }
    }

    public void renewOptionRoyal() {
        // 강림은 찬란한 빛의 결정이 재료임
        int requestItemID = 4031227;

        while (true) {
            // 재설정 드가자
            if (target.exchange(requestItemID, -100) > 0) {
                ExtraAbilityGrade grade = getPlayer().getExtraAbilityGrade();
                double rate = ExtraAbilityFactory.getGradeUpRate(grade, ExtraAbilityPayType.Donation);
                boolean legendaryOption = false;
                if (Randomizer.isSuccess((int) (rate * 10), 1000) && grade != ExtraAbilityGrade.Legendary) {
                    // 등업!
                    legendaryOption = true;
                    grade = ExtraAbilityGrade.getGrade(grade.getGradeID() + 1);
                    getPlayer().send(CField.addPopupSay(9062000, 5000, "#e#r" + grade.getDesc() + " 등급#k#n으로 등급업에 성공했습니다!", ""));
                    getPlayer().setExtraAbilityGrade(grade);
                    if (grade == ExtraAbilityGrade.Legendary) {
                        Center.Broadcast.broadcastMessage(CField.chatMsg(5, "[" + getPlayer().getName() + "]님이 엑스트라 어빌리티 '레전드리 등급'을 달성하였습니다!"));
                    }
                }

                NumberFormat nf = NumberFormat.getInstance();
                int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
                boolean enableExtra = getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()][5].getOption() != ExtraAbilityOption.None;
                if (!enableExtra) {
                    // 엑스트라 스탯이 없다면.
                    // 일정 확률로 달아주자.
                    if (grade == ExtraAbilityGrade.Legendary) {
                        // 레전드리 옵션에서 1% 확률로 달림
                        if (Randomizer.isSuccess(1)) {
                            enableExtra = true;
                        }
                    }
                }
                ExtraAbilityStatEntry[] entry = ExtraAbilityFactory.pickMeUpRoyal(grade, luckyPoint, getPlayer().getHgrade(), enableExtra);
                if (luckyPoint >= 30) {
                    luckyPoint -= 30;
                    getPlayer().updateOneInfo(787878, "lp", String.valueOf(luckyPoint));
                }
                getPlayer().checkExtraAbility();
                luckyPoint += 3;
                getPlayer().updateOneInfo(787878, "lp", String.valueOf(luckyPoint));

                String v5 = "#fs11##b엑스트라 어빌리티#k 재설정이 완료되었습니다.#r\r\n\r\n";

                v5 += "#e#k현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                int index = 0;

                StringBuilder sb = new StringBuilder("엑스트라 어빌리티 결과 (");
                sb.append("계정 : " + getClient().getAccountName());
                sb.append(", 캐릭터 : " + getPlayer().getName());
                sb.append(", 등잡 옵션 [");
                for (ExtraAbilityStatEntry e : entry) {
                    if (++index == 6) {
                        v5 += "\r\n#k#e[엑스트라 스탯]#n\r\n#r";
                    }
                    sb.append("등급 : ");
                    sb.append(getPlayer().getExtraAbilityGrade().getDesc());
                    sb.append(", ");
                    sb.append("[index:" + index);
                    sb.append("] " + String.format(e.getOption().getDesc(), e.getValue(), "%"));
                    if (index != 6) {
                        sb.append(",");
                    }
                    v5 += "  " + String.format(e.getOption().getDesc(), e.getValue(), "%");
                    v5 += "\r\n";
                }
                sb.append("])");
                LoggingManager.putLog(new EnchantLog(getPlayer(), 2, 0, 0, 14, 0, sb));
                getPlayer().setExtraAbilityStats(entry, getPlayer().getExtraAbilitySlot());
                getPlayer().checkExtraAbility();

                int remain = getPlayer().getItemQuantity(requestItemID, false);
                v5 += "\r\n#k#e보유중인 #z" + requestItemID + "# : " + remain + "개";
                v5 += "\r\n럭키 포인트 : ";
                if (luckyPoint >= 30) {
                    v5 += "#b" + luckyPoint + " (Lucky!!)#k";
                } else {
                    v5 += luckyPoint;
                }

                if (legendaryOption) {
                    self.sayOk(v5, ScriptMessageFlag.NoEsc);
                    break;
                } else {
                    /*if (type == ExtraAbilityPayType.Donation) {
                        v5 += "\r\n\r\n#b#e1,000 강림 포인트#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                    } else if (type == ExtraAbilityPayType.Promotion) {
                        v5 += "\r\n\r\n#b#e500 홍보 포인트#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                    } else if (type == ExtraAbilityPayType.Meso) {
                        v5 += "\r\n\r\n#b#e500,000,000 메소#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                    }*/
                    v5 += "\r\n\r\n#b#e#i" + requestItemID + "# #z" + requestItemID + "##k#n을 사용하여 옵션을 다시 설정하시겠습니까?";
                    v5 += "\r\n#b#L0#한 번 더 설정한다.#l\r\n#L1#대화를 종료한다.#l";
                    if (self.askMenu(v5) == 0) {
                        if (self.askMenu("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k\r\n\r\n#b#L0#한 번 더 설정한다.#l\r\n#L1#대화를 종료한다.#l") == 0) {
                            renewOptionRoyal();
                        }
                        break;
                    }
                    break;
                }
            } else {
                self.say("#fs11##zi" + requestItemID + "# #z" + requestItemID + "#이 부족하여 더 이상 재설정 할 수 없습니다.");
                break;
            }
        }
    }
    public void renewOption(ExtraAbilityPayType type) {
        // ㅇㅇ 돌려주세영
        while (true) {
            if (type == ExtraAbilityPayType.Donation) {
                if (getPlayer().getRealCash() < 1000) {
                    self.say("#b강림 포인트#k가 부족하여 재설정 할 수 없습니다.");
                    break;
                }
                getPlayer().gainRealCash(-1000, true);
            } else if (type == ExtraAbilityPayType.Promotion) {
                if (getPlayer().getHongboPoint() < 500) {
                    self.say("#b홍보 포인트#k가 부족하여 재설정 할 수 없습니다.");
                    break;
                }
                getPlayer().gainHongboPoint(-500, true);
            } else if (type == ExtraAbilityPayType.Meso) {
                if (getPlayer().getMeso() < 500000000L) {
                    self.say("#b메소#k가 부족하여 재설정 할 수 없습니다.");
                    break;
                }
                getPlayer().gainMeso(-500000000L, true);
            }

            // 재설정 드가자
            ExtraAbilityGrade grade = getPlayer().getExtraAbilityGrade();
            if (grade == ExtraAbilityGrade.Legendary) {
                if (type == ExtraAbilityPayType.Meso) {
                    self.say("#b레전드리 등급#k은 메소를 사용하여 재설정할 수 없습니다.");
                    return;
                }
            }

            double rate = ExtraAbilityFactory.getGradeUpRate(grade, type);
            if (Randomizer.isSuccess((int) (rate * 10), 1000) && grade != ExtraAbilityGrade.Legendary) {
                // 등업!
                grade = ExtraAbilityGrade.getGrade(grade.getGradeID() + 1);
                getPlayer().send(CField.addPopupSay(9062000, 5000, "#e#r" + grade.getDesc() + " 등급#k#n으로 등급업에 성공했습니다!", ""));
                getPlayer().setExtraAbilityGrade(grade);
                if (grade == ExtraAbilityGrade.Legendary) {
                    Center.Broadcast.broadcastMessage(CField.chatMsg(5, "[" + getPlayer().getName() + "]님이 엑스트라 어빌리티 '레전드리 등급'을 달성하였습니다!"));
                }
            }

            NumberFormat nf = NumberFormat.getInstance();
            int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
            ExtraAbilityStatEntry[] entry = ExtraAbilityFactory.pickMeUp(grade, luckyPoint, ExtraAbilityPayType.Donation);
            if (luckyPoint >= 30) {
                luckyPoint -= 30;
                getPlayer().updateOneInfo(787878, "lp", String.valueOf(luckyPoint));
            }
            boolean legendaryOption = false;
            if (grade == ExtraAbilityGrade.Legendary) {
                int result = ExtraAbilityFactory.checkAllMaxValue(entry);
                if (result == 2) {
                    String msg = getPlayer().getName() + "님의 엑스트라 어빌리티에서#r\r\n\r\n";
                    for (ExtraAbilityStatEntry e : entry) {
                        msg += String.format(e.getOption().getDesc(), e.getValue(), "%");
                        msg += "\r\n";
                    }
                    msg += "#k\r\n옵션이 등장했습니다!";
                    Center.Broadcast.broadcastMessage(CField.addPopupSay(9062000, 5000, msg, ""));
                    legendaryOption = true;
                } else if (result == 1) {
                    String msg = "엑스트라 어빌리티에서#r\r\n\r\n";
                    for (ExtraAbilityStatEntry e : entry) {
                        msg += String.format(e.getOption().getDesc(), e.getValue(), "%");
                        msg += "\r\n";
                    }
                    msg += "#k\r\n옵션이 등장했습니다!";
                    getPlayer().send(CField.addPopupSay(9062000, 5000, msg, ""));
                    legendaryOption = true;
                }
            }
            getPlayer().checkExtraAbility();
            if (type == ExtraAbilityPayType.Donation) {
                luckyPoint += 3;
            } else if (type == ExtraAbilityPayType.Promotion) {
                luckyPoint += 2;
            } else if (type == ExtraAbilityPayType.Meso) {
                luckyPoint += 1;
            }
            getPlayer().updateOneInfo(787878, "lp", String.valueOf(luckyPoint));

            String v5 = "#b엑스트라 어빌리티#k 재설정이 완료되었습니다.#r\r\n\r\n";

            v5 += "#e#k현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";

            StringBuilder sb = new StringBuilder("엑스트라 어빌리티 결과 (");
            sb.append("계정 : " + getClient().getAccountName());
            sb.append(", 캐릭터 : " + getPlayer().getName());
            sb.append(", 등잡 옵션 [");

            int index = 0;
            for (ExtraAbilityStatEntry e : entry) {
                v5 += "  " + String.format(e.getOption().getDesc(), e.getValue(), "%");
                v5 += "\r\n";

                index++;
                sb.append("등급 : ");
                sb.append(getPlayer().getExtraAbilityGrade().getDesc());
                sb.append(", ");
                sb.append("[index:" + index);
                sb.append("] " + String.format(e.getOption().getDesc(), e.getValue(), "%"));
                if (index != 2) {
                    sb.append(",");
                }
            }
            sb.append("])");
            LoggingManager.putLog(new EnchantLog(getPlayer(), 2, 0, 0, 14, 0, sb));

            getPlayer().setExtraAbilityStats(entry, getPlayer().getExtraAbilitySlot());
            getPlayer().checkExtraAbility();

            if (type == ExtraAbilityPayType.Donation) {
                v5 += "\r\n#k#e보유중인 포인트 : " + nf.format(getPlayer().getRealCash());
            } else if (type == ExtraAbilityPayType.Promotion) {
                v5 += "\r\n#k#e보유중인 포인트 : " + nf.format(getPlayer().getHongboPoint());
            } else if (type == ExtraAbilityPayType.Meso) {
                v5 += "\r\n#k#e보유중인 메소 : " + nf.format(getPlayer().getMeso());
            }
            v5 += "\r\n럭키 포인트 : ";
            if (luckyPoint >= 30) {
                v5 += "#b" + luckyPoint + " (Lucky!!)#k";
            } else {
                v5 += luckyPoint;
            }

            if (legendaryOption) {
                self.sayOk(v5, ScriptMessageFlag.NoEsc);
                break;
            } else {
                if (type == ExtraAbilityPayType.Donation) {
                    v5 += "\r\n\r\n#b#e1,000 강림 포인트#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                } else if (type == ExtraAbilityPayType.Promotion) {
                    v5 += "\r\n\r\n#b#e500 홍보 포인트#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                } else if (type == ExtraAbilityPayType.Meso) {
                    v5 += "\r\n\r\n#b#e500,000,000 메소#k#n를 사용하여 옵션을 다시 설정하시겠습니까?";
                }
                v5 += "\r\n#b#L0#한 번 더 설정한다.#l\r\n#L1#대화를 종료한다.#l";
                if (self.askMenu(v5) == 0) {
                    if (self.askMenu("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k\r\n\r\n#b#L0#한 번 더 설정한다.#l\r\n#L1#대화를 종료한다.#l") == 0) {
                        renewOption(type);
                    }
                    break;
                }
                break;
            }
        }

    }

    public void test() {
        initNPC(MapleLifeFactory.getNPC(9010017));
        NumberFormat nf = NumberFormat.getInstance();
        String v0 = "#fs11##e<엑스트라 어빌리티>#n\r\n현재 #b#h0##k님의 엑스트라 어빌리티 정보는 아래와 같습니다.\r\n\r\n";
        v0 += "#e현재 적용중인 프리셋 : #r" + getPlayer().getExtraAbilitySlot() + "번#k\r\n";
        v0 += "현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
        int index = 0;
        for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
            if (++index == 6) {
                v0 += "\r\n#k#e[엑스트라 스탯]#n#r\r\n";
            }
            v0 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
            v0 += "\r\n";
        }
        v0 += "\r\n#b#L0#현재 프리셋의 옵션을 재설정한다.#l\r\n";
        v0 += "#b#L1#프리셋을 변경한다.";
        boolean presetLock = getPlayer().getOneInfoQuestInteger(787878, "e_preset_open") == 0;
        if (presetLock) {
            v0 += " #e(잠김)#n";
        }
        v0 += "#l\r\n#b#L2#엑스트라 어빌리티에 대해 알아본다.#l\r\n";
        int v1 = self.askMenu(v0);
        int requestItemID = 4031227;
        switch (v1) {
            case 0: { // 옵션 재설정
                if (DBConfig.isGanglim) {
                    // 강림은 찬란한 빛의 결정이 재료임
                    int quantity = 100;
                    String v2 = "#fs11#";
                    boolean skipDesc = getPlayer().getOneInfoQuestInteger(787878, "skip_desc") == 1;
                    if (skipDesc) {
                        v2 = "#fs11##e<엑스트라 어빌리티>#n\r\n현재 #e#b" + getPlayer().getExtraAbilitySlot() + "번#k#n 프리셋이 적용중이며, 아래의 옵션이 적용중입니다.\r\n";
                    } else {
                        v2 = "#fs11##e<엑스트라 어빌리티>#n\r\n현재 #e#b" + getPlayer().getExtraAbilitySlot() + "번#k#n 프리셋이 적용중이며, 아래의 옵션이 적용중입니다.\r\n#r획득되는 럭키 포인트 및 이탈 옵션 유무, 등업 확률등#k 자세한 내용은 #b#e엑스트라 어빌리티에 대해 알아본다.#n#k를 참조해주시기 바랍니다.\r\n\r\n";
                    }
                    v2 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                    index = 0;
                    for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                        if (++index == 6) {
                            v2 += "\r\n#k#e[엑스트라 스탯]#n\r\n#r";
                        }
                        v2 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                        v2 += "\r\n";
                    }
                    v2 += "\r\n#b#L0##e#z" + requestItemID + "##n를 사용하여 현재 옵션을 재설정한다.#l\r\n";
                    if (skipDesc) {
                        v2 += "#L1#강화 시 상단 설명을 표시하겠습니다.#l\r\n";
                    } else {
                        v2 += "#L1#강화 시 상단 설명을 표시하지 않겠습니다.#l\r\n";
                    }
                    int v3 = self.askMenu(v2);
                    switch (v3) {
                        case 0: { // 찬란한 빛의 결정
                            String v4 = "#fs11##e<엑스트라 어빌리티>#n\r\n#b#z" + requestItemID + "# " + quantity + "개#k를 사용하여 엑스트라 어빌리티 옵션을 재설정하시겠습니까?\r\n\r\n";

                            v4 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                            index = 0;
                            for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                                if (++index == 6) {
                                    v4 += "\r\n#k#e[엑스트라 스탯]#n\r\n#r";
                                }
                                v4 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                                v4 += "\r\n";
                            }

                            int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
                            int q = getPlayer().getItemQuantity(requestItemID, false);
                            v4 += "\r\n#k#e보유중인 #z" + requestItemID + "# : " + q + "개";
                            v4 += "\r\n럭키 포인트 : ";
                            if (luckyPoint >= 30) {
                                v4 += "#b" + luckyPoint + " (Lucky!!)#k";
                            } else {
                                v4 += luckyPoint;
                            }
                            if (self.askYesNo(v4) == 1) {
                                if (self.askYesNo("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k") == 1) {
                                    renewOptionRoyal();
                                    return;
                                }
                            }
                            break;
                        }
                        case 1: { // 상단 설명 on/off
                            if (skipDesc) {
                                self.say("#fs11##e#b강화 시 상단 설명이 표시됨#k#n으로 설정되었습니다.");
                                getPlayer().updateOneInfo(787878, "skip_desc", "0");
                            } else {
                                self.say("#fs11##e#b강화 시 상단 설명이 표시되지 않음#k#n으로 설정되었습니다.");
                                getPlayer().updateOneInfo(787878, "skip_desc", "1");
                            }
                            break;
                        }
                    }
                } else {
                    String v2 = "";
                    boolean skipDesc = getPlayer().getOneInfoQuestInteger(787878, "skip_desc") == 1;
                    if (skipDesc) {
                        v2 = "#e<엑스트라 어빌리티>#n\r\n현재 #e#b" + getPlayer().getExtraAbilitySlot() + "번#k#n 프리셋이 적용중이며, 아래의 옵션이 적용중입니다.\r\n";
                    } else {
                        v2 = "#e<엑스트라 어빌리티>#n\r\n현재 #e#b" + getPlayer().getExtraAbilitySlot() + "번#k#n 프리셋이 적용중이며, 아래의 옵션이 적용중입니다. #b세 가지 종류#k로 옵션을 재설정할 수 있으며, 종류별로 #r획득되는 럭키 포인트 및 이탈 옵션 유무, 등업 확률등#k이 달라지며 자세한 내용은 #b#e엑스트라 어빌리티에 대해 알아본다.#n#k를 참조해주시기 바랍니다.\r\n\r\n";
                    }
                    v2 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                    for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                        v2 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                        v2 += "\r\n";
                    }
                    v2 += "\r\n#b#L0##e강림 포인트#n를 사용하여 현재 옵션을 재설정한다.#l\r\n";
                    v2 += "#L1##e홍보 포인트#n를 사용하여 현재 옵션을 재설정한다.#l\r\n";
                    v2 += "#L2##e메소#n를 사용하여 현재 옵션을 재설정한다.#l\r\n";
                    if (skipDesc) {
                        v2 += "#L3#강화 시 상단 설명을 표시하겠습니다.#l\r\n";
                    } else {
                        v2 += "#L3#강화 시 상단 설명을 표시하지 않겠습니다.#l\r\n";
                    }
                    int v3 = self.askMenu(v2);
                    switch (v3) {
                        case 0: { // 진 포인트
                            String v4 = "#e<엑스트라 어빌리티>#n\r\n#b1,000 강림 포인트#k를 사용하여 엑스트라 어빌리티 옵션을 재설정하시겠습니까?\r\n\r\n";

                            v4 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                            for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                                v4 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                                v4 += "\r\n";
                            }

                            int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
                            v4 += "\r\n#k#e보유중인 포인트 : " + nf.format(getPlayer().getRealCash());
                            v4 += "\r\n럭키 포인트 : ";
                            if (luckyPoint >= 30) {
                                v4 += "#b" + luckyPoint + " (Lucky!!)#k";
                            } else {
                                v4 += luckyPoint;
                            }
                            if (self.askYesNo(v4) == 1) {
                                if (self.askYesNo("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k") == 1) {
                                    renewOption(ExtraAbilityPayType.Donation);
                                    return;
                                }
                            }
                            break;
                        }
                        case 1: { // 홍보 포인트
                            String v4 = "#e<엑스트라 어빌리티>#n\r\n#b500 홍보 포인트#k를 사용하여 엑스트라 어빌리티 옵션을 재설정하시겠습니까?\r\n\r\n";

                            v4 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                            for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                                v4 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                                v4 += "\r\n";
                            }

                            int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
                            v4 += "\r\n#k#e보유중인 포인트 : " + nf.format(getPlayer().getHongboPoint());
                            v4 += "\r\n럭키 포인트 : ";
                            if (luckyPoint >= 30) {
                                v4 += "#b" + luckyPoint + " (Lucky!!)#k";
                            } else {
                                v4 += luckyPoint;
                            }
                            if (self.askYesNo(v4) == 1) {
                                if (self.askYesNo("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k") == 1) {
                                    renewOption(ExtraAbilityPayType.Promotion);
                                    return;
                                }
                            }
                            break;
                        }
                        case 2: { // 메소
                            String v4 = "#e<엑스트라 어빌리티>#n\r\n#b500,000,000 메소#k를 사용하여 엑스트라 어빌리티 옵션을 재설정하시겠습니까?\r\n\r\n";

                            v4 += "#e현재 등급 : #r" + getPlayer().getExtraAbilityGrade().getDesc() + "#k#n#r\r\n\r\n";
                            for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                                v4 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                                v4 += "\r\n";
                            }

                            int luckyPoint = getPlayer().getOneInfoQuestInteger(787878, "lp");
                            v4 += "\r\n#k#e보유중인 메소 : " + nf.format(getPlayer().getMeso());
                            v4 += "\r\n럭키 포인트 : ";
                            if (luckyPoint >= 30) {
                                v4 += "#b" + luckyPoint + " (Lucky!!)#k";
                            } else {
                                v4 += luckyPoint;
                            }
                            if (self.askYesNo(v4) == 1) {
                                if (getPlayer().getExtraAbilityGrade() == ExtraAbilityGrade.Legendary) {
                                    self.say("#b레전드리 등급#k은 메소를 사용하여 재설정할 수 없습니다.");
                                    return; // 레전드리는 메소로 불가
                                }
                                if (self.askYesNo("#fs11##r#e정말 재설정 하시겠습니까? 재설정 시 현재 옵션은 사라지게 됩니다.#n#k") == 1) {
                                    renewOption(ExtraAbilityPayType.Meso);
                                    return;
                                }
                            }
                            break;
                        }
                        case 3: { // 상단 설명 on/off
                            if (skipDesc) {
                                self.say("#e#b강화 시 상단 설명이 표시됨#k#n으로 설정되었습니다.");
                                getPlayer().updateOneInfo(787878, "skip_desc", "0");
                            } else {
                                self.say("#e#b강화 시 상단 설명이 표시되지 않음#k#n으로 설정되었습니다.");
                                getPlayer().updateOneInfo(787878, "skip_desc", "1");
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case 1: { // 프리셋 변경
                if (presetLock) {
                    String v2 = "#fs11##e<엑스트라 어빌리티>#n\r\n현재 엑스트라 어빌리티 프리셋이 잠겨있습니다. #b캐시#k를 사용하여 잠금을 해제할 수 있습니다.#b\r\n\r\n";
                    if (DBConfig.isGanglim) {
                        v2 += "#L0##e50,000 캐시#n를 사용하여 해제한다.#l\r\n";
                        v2 += "#L1#대화를 종료한다.#l\r\n";
                        int v3 = self.askMenu(v2);
                        if (v3 == 1) {
                            return;
                        }
                        switch (v3) {
                            case 0: {
                                if (1 == self.askYesNo("#fs11##e<엑스트라 어빌리티>#n\r\n#b50,000 캐시#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하시겠습니까?\r\n\r\n#e보유중인 포인트 : " + nf.format(getPlayer().getCashPoint()))) {
                                    if (getPlayer().getCashPoint() < 50000) {
                                        self.say("강림 캐시가 부족하여 잠금 해제를 할 수 없습니다.");
                                        return;
                                    }
                                    getPlayer().gainCashPoint(-50000);
                                    getPlayer().updateOneInfo(787878, "e_preset_open", "1");
                                    self.say("#b50,000 캐시#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하였습니다.");
                                    return;
                                }
                                break;
                            }
                        }
                    } else {
                        v2 += "#L0#50,000 #e강림 포인트#n를 사용하여 해제한다.#l\r\n";
                        v2 += "#L1#50,000 #e홍보 포인트#n를 사용하여 해제한다.#l\r\n";
                        v2 += "#L2#대화를 종료한다.#l\r\n";
                        int v3 = self.askMenu(v2);
                        if (v3 == 2) {
                            return;
                        }
                        switch (v3) {
                            case 0: {
                                if (1 == self.askYesNo("#e<엑스트라 어빌리티>#n\r\n#b50,000 강림 포인트#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하시겠습니까?\r\n\r\n#e보유중인 포인트 : " + nf.format(getPlayer().getRealCash()))) {
                                    if (getPlayer().getRealCash() < 50000) {
                                        self.say("강림 포인트가 부족하여 잠금 해제를 할 수 없습니다.");
                                        return;
                                    }
                                    getPlayer().gainRealCash(-50000, true);
                                    getPlayer().updateOneInfo(787878, "e_preset_open", "1");
                                    self.say("#b50,000 강림 포인트#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하였습니다.");
                                    return;
                                }
                                break;
                            }
                            case 1: {
                                if (1 == self.askYesNo("#e<엑스트라 어빌리티>#n\r\n#b50,000 강림 포인트#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하시겠습니까?\r\n\r\n#e보유중인 포인트 : " + nf.format(getPlayer().getHongboPoint()))) {
                                    if (getPlayer().getHongboPoint() < 50000) {
                                        self.say("홍보 포인트가 부족하여 잠금 해제를 할 수 없습니다.");
                                        return;
                                    }
                                    getPlayer().gainHongboPoint(-50000, true);
                                    getPlayer().updateOneInfo(787878, "e_preset_open", "1");
                                    self.say("#b50,000 홍보 포인트#k를 사용하여 엑스트라 어빌리티 프리셋 잠금을 해제하였습니다.");
                                    return;
                                }
                                break;
                            }
                        }
                    }
                } else {
                    int preset = getPlayer().getExtraAbilitySlot();
                    long meso = 3000000000L;
                    if (DBConfig.isGanglim) {
                        meso = 10000000000L;
                    }
                    String v2 = "#fs11##e<엑스트라 어빌리티>\r\n\r\n현재 적용중인 프리셋 : #r" + preset + "번#k\r\n";
                    v2 += "#e보유중인 메소 : #r" + nf.format(getPlayer().getMeso()) + "#k#n\r\n\r\n";
                    v2 += "#b" + nf.format(meso) + "메소#k를 사용하여 #b" + (preset ^ 1) + " 번#k 프리셋으로 변경하시겠습니까?\r\n\r\n";
                    if (1 == self.askYesNo(v2)) {
                        if (getPlayer().getMeso() < meso) {
                            self.say("메소가 부족하여 프리셋 변경을 할 수 없습니다.");
                            return;
                        }
                        getPlayer().gainMeso(-meso, true);
                        getPlayer().setExtraAbilitySlot((preset ^ 1));
                        getPlayer().checkExtraAbility();
                        String v3 = "#fs11##e<엑스트라 어빌리티>#n\r\n\r\n#b#e" + getPlayer().getExtraAbilitySlot() + "번 프리셋#n#k으로 변경되었습니다.#r\r\n\r\n";
                        for (ExtraAbilityStatEntry entry : getPlayer().getExtraAbilityStats()[getPlayer().getExtraAbilitySlot()]) {
                            v3 += "  " + String.format(entry.getOption().getDesc(), entry.getValue(), "%");
                            v3 += "\r\n";
                        }
                        self.say(v3);
                    }
                }
                break;
            }
            case 2: { // 어빌에 대해
                if (DBConfig.isGanglim) {
                    self.say("#fs11##b엑스트라 어빌리티 시스템#k은 캐릭터의 추가적인 성장 수단입니다.\r\n\r\n엑스트라 어빌리티는 #e#r여섯 개의 랜덤 옵션#k#n을 부여하는 방식으로, 재화를 소모하여 부여되는 옵션의 종류를 변경할 수 있습니다.\r\n\r\n옵션 변경은 #e#b찬란한 빛의 결정#k#n을 재화로 이용 가능합니다.");
                    self.say("#fs11#네 번째 줄 옵션은 #eVVIP Classic#n 등급부터 등장하며,\r\n다섯 번째 줄 옵션은 #eMVP Prime#n 등급부터 등장합니다.\r\n여섯 번째 엑스트라 옵션은 #b'레전드리' 등급#k에서 재설정시\r\n#e1% 확률#n로 등장하며, 한 번 출현하면 이후 부터는 계속해서 재설정됩니다.");
                    String v2 = "#fs11#보다 자세한 정보는 아래 선택지를 통해 확인하실 수 있습니다.\r\n\r\n#b#L0#엑스트라 어빌리티 프리셋에 관해 알고 싶습니다.#l\r\n#b#L1#엑스트라 어빌리티 등급별 옵션을 알고 싶습니다.#l\r\n#b#L2#엑스트라 어빌리티 등급별 확률을 알고 싶습니다.#l\r\n#L3#엑스트라 어빌리티 럭키 포인트에 대해 알고 싶습니다.#l";
                    int v3 = self.askMenu(v2);
                    switch (v3) {
                        case 0: {
                            self.say("#fs11#엑스트라 어빌리티 프리셋은 초기 1개의 프리셋을 기본으로 제공하며, #b캐시#k를 #e50,000#n 소모하여 1칸을 추가 확장할 수 있습니다.\r\n\r\n각 프리셋별로 다른 옵션을 설정하여 이용할 수 있으며, 프리셋을 변경할 때마다 #e100억 메소#n를 소모합니다.\r\n\r\n#e#r※ 옵션 변경은 현재 사용 중인 프리셋을 기준으로 적용되니 이용에 주의하시기 바랍니다.");
                            break;
                        }
                        case 1: {
                            self.say("#fs11##e[ 엑스트라 ]#n\r\n\r\n#b- 재사용 대기시간 1~2초 감소\r\n- 부활 시 무적 시간 2~4초 증가\r\n- 몬스터 개체수 1.5배 증가\r\n- 최종 데미지 5~20%\r\n- 경험치 획득량 30~50%\r\n- 메소 획득량 30~50%\r\n- 아이템 드롭률 30~50%\r\n- 상태 이상 내성");
                            self.say("#fs11##e[ 레전드리 ]#n\r\n\r\n#b- 재사용 대기시간 1~2초 감소\r\n- 경험치 획득량 30~50%\r\n- 아이템 드롭률 20~30%\r\n- 메소 획득량 20~30%\r\n- 보스몬스터 공격시 데미지 30~40%\r\n- 몬스터 방어력 무시 30~40%\r\n- 크리티컬 확률 12%\r\n- 공격력/마력 12~16%\r\n- 공격력/마력 +100~150\r\n- 올스탯 +18~25%\r\n- 올스탯 +200~300\r\n- HP +15~18%");
                            self.say("#fs11##e[ 유니크 ]#n\r\n\r\n#b- 경험치 획득량 10~20%\r\n- 아이템 드롭률 10~20%\r\n- 메소 획득량 10~20%\r\n- 보스몬스터 공격시 데미지 20~30%\r\n- 몬스터 방어력 무시 20~30%\r\n- 크리티컬 확률 9~12%\r\n- 공격력/마력 9~12%\r\n- 공격력/마력 +80~100\r\n- 올스탯 +15~21%\r\n- 올스탯 +100~150\r\n- HP +12~15%");
                            self.say("#fs11##e[ 에픽 ]#n\r\n\r\n#b- 보스몬스터 공격시 데미지 10~20%\r\n- 몬스터 방어력 무시 8~12%\r\n- 크리티컬 확률 6~9%\r\n- 공격력/마력 6~9%\r\n- 공격력/마력 +50~80\r\n- 올스탯 +12~15%\r\n- 올스탯 +80~100\r\n- HP +9~12%");
                            self.say("#fs11##e[ 레어 ]#n\r\n\r\n#b- 피격 시 받는 데미지 60~80%\r\n- STR, DEX, INT, LUK +80~100\r\n- 올스탯 +50~80\r\n- HP +6~9%\r\n- HP +3000~5000\r\n- 공격력/마력 +3~6%\r\n- 공격력/마력 +30~50");
                            self.say("#fs11##b모든 줄#k의 옵션은 현재 등급에 맞는 옵션이 등장하며, 옵션 등장 확률이 다른 옵션이 존재합니다.");
                            break;
                        }
                        case 2: {
                            self.say("#fs11##e[레어 → 에픽 등급 상승 확률]#n\r\n#b15% 확률로 증가\r\n\r\n\r\n#k#e[에픽 → 유니크 등긍 삽승 확률]#n\r\n#b3.8% 확률로 증가\r\n\r\n\r\n#k#e[유니크 → 레전드리 등급 상승 확률]#n\r\n#b1% 확률로 증가\r\n#e[엑스트라 등급 등장 확률]#n\r\n#b'레전드리' 등급에서 1% 확률로 등장");
                            break;
                        }
                        case 3: {
                            self.say("#fs11#럭키 포인트는 사용한 재화에 따라 차등 획득하며 아래와 같이 획득할 수 있습니다.\r\n\r\n#e#r#z" + requestItemID + "# 사용 시 3포인트");
                            self.say("#fs11#강화 시 얻은 럭키 포인트가 #e#b30 포인트#n#k를 넘을 시 자동으로 30포인트가 차감되며, 1~5줄이 해당 옵션의 최대 수치로 등장합니다.\r\n\r\n#e5줄 럭키의 확률은 10%\r\n4줄 럭키의 확률은 10%\r\n3줄 럭키의 확률은 15%\r\n2줄 럭키의 확률은 25%\r\n1줄 럭키의 확률은 40% 입니다.");
                            break;
                        }
                    }
                } else {
                    self.say("#fs11##b엑스트라 어빌리티 시스템#k은 캐릭터의 추가적인 성장 수단입니다.\r\n\r\n엑스트라 어빌리티는 #e#r세 개의 랜덤 옵션#k#n을 부여하는 방식으로, 재화를 소모하여 부여되는 옵션의 종류를 변경할 수 있습니다.\r\n\r\n옵션 변경은 #e#b강림 포인트, 홍보 포인트, 메소#k#n 등의 재화로 이용 가능하며, 소모하는 재화에 따라 등장하는 옵션의 최대 등급 및 수치에 차이가 발생합니다.");
                    self.say("#fs11#각 재화별 옵션 변경에 소모하는 비용은 아래와 같습니다.\r\n\r\n#r강림 포인트 : 1,000\r\n홍보 포인트 : 500\r\n메소 : 500,000,000");
                    String v2 = "#fs11#보다 자세한 정보는 아래 선택지를 통해 확인하실 수 있습니다.\r\n\r\n#b#L0#엑스트라 어빌리티 프리셋에 관해 알고 싶습니다.#l\r\n#b#L1#엑스트라 어빌리티 등급별 옵션을 알고 싶습니다.#l\r\n#b#L2#엑스트라 어빌리티 등급별 확률을 알고 싶습니다.#l\r\n#L3#엑스트라 어빌리티 럭키 포인트에 대해 알고 싶습니다.#l";
                    int v3 = self.askMenu(v2);
                    switch (v3) {
                        case 0: {
                            self.say("#fs11#엑스트라 어빌리티 프리셋은 초기 1개의 프리셋을 기본으로 제공하며, #b강림 포인트#k 또는 #b홍보 포인트#k를 #e50,000#n 소모하여 1칸을 추가 확장할 수 있습니다.\r\n\r\n각 프리셋별로 다른 옵션을 설정하여 이용할 수 있으며, 프리셋을 변경할 때마다 #e30억 메소#n를 소모합니다.\r\n\r\n#e#r※ 옵션 변경은 현재 사용 중인 프리셋을 기준으로 적용되니 이용에 주의하시기 바랍니다.");
                            break;
                        }
                        case 1: {
                            self.say("#fs11##e[레전드리]#n\r\n\r\n#b- 경험치 10~20%\r\n- 아이템 드롭률 10~20%\r\n- 메소 획득량 10~20%\r\n- 재사용 대기시간 1~2초 감소\r\n- 크리티컬 데미지 5~8%\r\n- 공격력/마력 9~12%\r\n- 보스 공격 시 데미지 20~30%\r\n- 몬스터 방어력 무시 20~30%\r\n- 크리티컬 확률 9~12%\r\n- 올스탯 +15~21%\r\n- hp +12~15%\r\n- 공격력/마력 +80~100\r\n- 올스탯 +100~150");
                            self.say("#fs11##e[ 유니크 ]#n\r\n\r\n#b- 보스 공격 시 데미지 10~20%\r\n- 몬스터 방어력 무시 8~12%\r\n- 크리티컬 확률 6~9%\r\n- 공격력/마력 6~9%\r\n- 올스탯 +12~15%\r\n- hp +9~12%\r\n- 공격력/마력 +50~80\r\n- 올스탯 +80~100");
                            self.say("#fs11##e[ 에픽 ]#n\r\n\r\n#b- 공격력/마력 3~6%\r\n- 올스탯 +9~12%\r\n- hp +6~9%\r\n- 피격 시 받는 데미지 60~80%\r\n- str, dex, int, luk +80~100\r\n- 올스탯 +50~80\r\n- hp +3000~5000\r\n- 공격력/마력 +30~50");
                            self.say("#fs11##e[ 레어 ]#n\r\n\r\n#b- 피격 시 받는 데미지 40~60%\r\n- str, dex, int, luk +50~80\r\n- 올스탯 +20~50\r\n- hp +1000~2000\r\n- 공격력/마력 +10~30");
                            self.say("#fs11##b첫 번째 줄#k은 #e현재 등급의 옵션#n이 등장하고, #b두 번째 / 세 번째 줄#k은 #e현재 등급보다 한 단계 아래 옵션#n이 출현하며, 사용 한 재화에 따라 이탈 옵션이 등장할 수 있습니다.");
                            self.say("#fs11##b두 번째 줄 옵션#k은 #e강림 포인트 및 홍보 포인트#n 사용 시 #r30% 확률#k로 #e현재 등급의 옵션#n이 등장하고, #b세 번째 줄 옵션#k은 #e강림 포인트#n 사용 시 #r10% 확률#k로 #e현재 등급의 옵션#n이 등장합니다.");
                            break;
                        }
                        case 2: {
                            self.say("#fs11##e[레어 → 에픽 등급 상승 확률]#n\r\n#b강림 포인트 : 15%\r\n홍보 포인트 : 10%\r\n메소 : 5%\r\n\r\n\r\n#k#e[에픽 → 유니크 등긍 삽승 확률]#n\r\n#b강림 포인트 : 3.8%\r\n홍보 포인트 : 2.5%\r\n메소 : 0.9%\r\n\r\n\r\n#k#e[유니크 → 레전드리 등급 상승 확률]#n\r\n#b강림 포인트 : 1%\r\n홍보 포인트 : 0.7%\r\n메소 : 불가");
                            break;
                        }
                        case 3: {
                            self.say("#fs11#럭키 포인트는 사용한 재화에 따라 차등 획득하며 아래와 같이 획득할 수 있습니다.\r\n\r\n#e#r메소 사용 시 : 1포인트\r\n홍보 포인트 사용 시 : 2포인트\r\n강림 포인트 사용 시 : 3포인트");
                            self.say("#fs11#강화 시 얻은 럭키 포인트가 #e#b30 포인트#n#k를 넘을 시 자동으로 30포인트가 차감되며, 1~3줄이 해당 옵션의 최대 수치로 등장합니다.\r\n\r\n#e3줄 럭키의 확률은 20%\r\n2줄 럭키의 확률은 30%\r\n1줄 럭키의 확률은 50% 입니다.");
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    int[] equipList = new int[]{
            1012643, 1022289, 1132311, 1162079, 1113307, 1122431, 1032314, 1182282, 1190544
    };
    int[] baseEquipList = new int[]{
            1012632, 1022278, 1132308, 1162083, 1113306, 1122430, 1032316, 1182285, 1190555
    };
    int[] consumeList = new int[]{
            2435873, 2435874, 2435875, 2435876
    };
    int[] consumePrice = new int[]{
            120, 220, 450, 650
    };
    public void npc_2570100() {
        initNPC(MapleLifeFactory.getNPC(2570100));
        NumberFormat nf = NumberFormat.getNumberInstance();
        String v0 = "구경해 보겠나. 우리가 발견한 알 수 없는 기운이 느껴지는 아이템을. …자네가 가지고 있는 그것에서도 비슷한 기운이 느껴지는군. 괜찮다면 내가 가진 아이템과 교환해 주겠네.\r\n검은 기운이 느껴지는 장비라면 이 기운과 조합하여 새로운 아이템을 만들 수 있을 것 같군. 한 번 시도해 보겠나?\r\n\r\n";
        v0 += "#b#L0##e칠흑의 기운#n을 #e헬 포인트#n로 교환하고 싶습니다.#l\r\n";
        v0 += "#L1##e헬 포인트 상점#n을 이용하고 싶습니다.#l\r\n";
        v0 += "#L2##e각성한 힘이 깃든 아이템#n을 제작하고 싶습니다.#l\r\n";
        v0 += "#L3#다음에 다시 찾아오겠습니다.#l";
        int v1 = self.askMenu(v0);
        switch (v1) {
            case 0: {
                int count = getPlayer().getItemQuantity(4036454, false);
                int hellPoint = getPlayer().getOneInfoQuestInteger(1234999, "hp");
                String v2 = "자네가 가지고 있는 #b#i4036454# #z4036454##k에서는 좋지 않은 느낌이 드는군. …그래. 헬 포인트라고 부르기로 하지. 자네가 가진 #b#z4036454##k 하나를 #b3 헬 포인트#k로 바꿔 주겠네. 앞으로 자네와의 교환에서 사용하게 될 포인트가 될 테니 필요한 만큼 건네 주게나.\r\n\r\n";
                v2 += "#b#e보유 중인 #z4036454# 갯수 : " + count + "\r\n";
                v2 += "보유 중인 헬 포인트 : " + nf.format(hellPoint) + "#n\r\n\r\n";
                v2 += "#b#L0##e#z4036454##n을 #e헬 포인트#n로 교환해주세요.#l\r\n";
                v2 += "#L1#다음에 다시 찾아오겠습니다.#l";
                int v3 = self.askMenu(v2);
                if (v3 == 0) {
                    count = getPlayer().getItemQuantity(4036454, false);
                    if (count <= 0) {
                        self.say("자네에게서는 어떠한 기운도 느껴지지 않는군. 허상이라도 보았나?");
                        return;
                    }
                    String v4 = "자네는 #b#i4036454# #z4036454#을 " + count + "개#k 보유하고 있군. 몇 개를 #b헬 포인트#k로 교환하고 싶나?\r\n";
                    int v5 = self.askNumber(v4, count, 1, count);
                    if (v5 > count) {
                        return; //Hack
                    }
                    if (1 == self.askYesNo("#b #b#i4036454# #z4036454# " + v5 + "개#k를 #b" + (3 * v5) + " 헬 포인트#k로 교환하겠나?")) {
                        if (1 == target.exchange(4036454, -v5)) {
                            getPlayer().updateOneInfo(1234999, "hp", String.valueOf(hellPoint + (3 * v5)));
                            self.say("#b" + (3 * v5) + " 헬 포인트#k로 교환해줬으니 확인해보게나.");
                        } else {
                            self.say("알 수 없는 이유로 교환에 실패했군. 다시 시도해보겠나?");
                        }
                    }
                }
                break;
            }
            case 1: {
                int hellPoint = getPlayer().getOneInfoQuestInteger(1234999, "hp");
                String v2 = "탐욕스러운 자의 말로는 좋지 않아. 원하는 아이템을 하나만 골라 보게. 기사는 한 번 내린 결정을 번복하지 않는다네. 자네도 결정할 때는 신중하게나. 후회할 때는 이미 늦었을 테니.\r\n#b";
                v2 += "#e보유 중인 헬 포인트 : " + nf.format(hellPoint) + "P\r\n\r\n";
                v2 += "#k#e[아이템 리스트]#n#b\r\n";
                for (int i = 0; i < consumeList.length; ++i) {
                    v2 += "#L" + i + "##i" + consumeList[i] + "# #z" + consumeList[i] + "# #r(" + consumePrice[i] + "P)#b#l\r\n";
                }
                int v3 = self.askMenu(v2);
                hellPoint = getPlayer().getOneInfoQuestInteger(1234999, "hp");
                if (consumePrice[v3] > hellPoint) {
                    self.say("#b헬 포인트#k가 부족하군. 원하는 것을 얻기 위해서는 대가를 치러야 하지 않겠나?");
                    return;
                }
                if (1 == target.exchange(consumeList[v3], 1)) {
                    getPlayer().updateOneInfo(1234999, "hp", String.valueOf(hellPoint - consumePrice[v3]));
                    self.say("여기 있네. 자네의 인벤토리를 확인해보게나.");
                } else {
                    self.say("자네의 인벤토리가 가득찬 것 같군.");
                }
                break;
            }
            case 2: {
                String v2 = "금방이라도 사라질 듯 불안정한 기운이군. 한 번에 성공하지 못할 수도 있다네. 정말 시도하겠나? 두려울 때는 도망치는 것도 방법이겠지. 하지만 성공하면 지금보다 강한 힘을 얻을 수 있을 걸세.\r\n\r\n#b(제작 실패 시 재료로 사용된 칠흑 장비를 제외한 포인트가 사라지며, 제작 성공 시 재료로 사용된 칠흑 장비의 옵션이 각성한 칠흑 장비로 전승됩니다.)#n#b\r\n\r\n";
                v2 += "#k#e[아이템 리스트]#n#b\r\n";
                for (int i = 0; i < equipList.length; ++i) {
                	if (i == 3) continue;
                    v2 += "#L" + i + "##i" + equipList[i] + "# #z" + equipList[i] + "##l\r\n";
                }
                v2 += "\r\n\r\n#r※ 해당 장비는 제작 시 교환불가상태로 변경됩니다.";
                int v3 = self.askMenu(v2);
                if (v3 == 3) return;
                String pointName = "";
                String pointKey = "";
                int baseEquipID = baseEquipList[v3];
                int equipID = equipList[v3];
                int p = 25;
                switch (v3) {
                    case 0: {
                        pointName = "스우 포인트";
                        pointKey = "hell_swoo_point";
                        break;
                    }
                    case 1: {
                        pointName = "데미안 포인트";
                        pointKey = "hell_demian_point";
                        break;
                    }
                    case 2:
                        pointName = "루시드 포인트";
                        pointKey = "hell_lucid_point";
                        break;
                    case 3:
                        pointName = "윌 포인트";
                        pointKey = "hell_will_point";
                        break;
                    case 4:
                        pointName = "더스크 포인트";
                        pointKey = "hell_dusk_point";
                        break;
                    case 5:
                        pointName = "진 힐라 포인트";
                        pointKey = "hell_jinhillah_point";
                        break;
                    case 6:
                        pointName = "듄켈 포인트";
                        pointKey = "hell_dunkel_point";
                        break;
                    case 7:
                        pointName = "검은마법사 포인트";
                        pointKey = "hell_bm_point";
                        p = 5;
                        break;
                    case 8:
                        pointName = "세렌 포인트";
                        pointKey = "hell_seren_point";
                        break;
                }

                if (pointName.isEmpty() || pointKey.isEmpty()) {
                    self.say("알 수 없는 오류가 발생하였습니다.");
                    return;
                }
                String v4 = "#b#i" + equipID + "# #z" + equipID + "##k를 얻고 싶나보군. 강한 힘을 얻기 위해서는 #b#z" + baseEquipID + "# 1개#k와 #b" + p + " " + pointName + "#k, #b#z4036454# 50개#k가 필요하다네. 제작에 실패하면 재료로 사용한 장비는 남지만 포인트와 강림 칠흑의 기운은 사라지게 되지. 시도해 보겠나?\r\n\r\n\n";
                int point = getPlayer().getOneInfoQuestInteger(787777, pointKey);
                v4 += "#b#e보유 중인 " + pointName + " : " + nf.format(point) + "P#n#k\r\n\r\n#e#r※ 인벤토리 정렬 기준으로 가장 앞에 있는 장비를 재료로 사용합니다.";
                if (1 == self.askYesNo(v4)) {
                    Item item = null;
                    if (baseEquipID == 1162083) {
                        for (int i = 1162080; i <= 1162083; ++i) {
                            item = getPlayer().getInventory(MapleInventoryType.EQUIP).findByIdWithoutLock(i);
                            if (item != null) {
                                baseEquipID = item.getItemId();
                                break;
                            }
                        }
                    } else if (baseEquipID == 1190555) {
                        for (int i = 1190555; i <= 1190559; ++i) {
                            item = getPlayer().getInventory(MapleInventoryType.EQUIP).findByIdWithoutLock(i);
                            if (item != null) {
                                baseEquipID = item.getItemId();
                                break;
                            }
                        }
                    }
                    int count = getPlayer().getInventory(MapleInventoryType.EQUIP).countByIdWithoutLock(baseEquipID);
                    if (count <= 0) {
                        self.say("자네에게서는 어떠한 기운도 느껴지지 않는군. 허상이라도 보았나?");
                        return;
                    }
                    if (point < p) {
                        self.say(pointName + "가 부족해보이는군.");
                        return;
                    }
                    int count2 = getPlayer().getInventory(MapleInventoryType.ETC).countByIdWithoutLock(4036454);
                    int removeQty = 50;
                    if (count2 < removeQty) {
                        self.say("#b#i4036454# #z4036454#이 부족해보이는군.");
                        return;
                    }
                    // 50% 확률로 제작
                    getPlayer().updateOneInfo(787777, pointKey, String.valueOf(point - p));
                    if (Randomizer.isSuccess(50)) {
                        item = getPlayer().getInventory(MapleInventoryType.EQUIP).findByIdWithoutLock(baseEquipID);
                        if (item == null) {
                            self.say("자네에게서는 어떠한 기운도 느껴지지 않는군. 허상이라도 보았나?");
                            return;
                        }
                        if (1 == target.exchange(baseEquipID, -1, 4036454, -removeQty)) {
                            // 장비의 옵션을 가져오장
                            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            Equip equip = (Equip) item;
                            Item newItem = ii.getEquipById(equipID);
                            if (newItem == null) {
                                self.say("알 수 없는 오류가 발생하였습니다.");
                                return;
                            }
                            Equip newEquip = (Equip) newItem;
                            Item baseItem = ii.getEquipById(item.getItemId());
                            if (baseItem == null) {
                                self.say("알 수 없는 오류가 발생하였습니다.");
                                return;
                            }
                            Equip baseEquip = (Equip) baseItem;
                            int str = newEquip.getStr() - baseEquip.getStr();
                            int dex = newEquip.getDex() - baseEquip.getDex();
                            int int_ = newEquip.getInt() - baseEquip.getInt();
                            int luk = newEquip.getLuk() - baseEquip.getLuk();
                            int pad = newEquip.getWatk() - baseEquip.getWatk();
                            int mad = newEquip.getMatk() - baseEquip.getMatk();

                            newEquip.set2(equip);
                            newEquip.setStr((short) (newEquip.getStr() + str));
                            newEquip.setDex((short) (newEquip.getDex() + dex));
                            newEquip.setInt((short) (newEquip.getInt() + int_));
                            newEquip.setLuk((short) (newEquip.getLuk() + luk));
                            newEquip.setWatk((short) (newEquip.getWatk() + pad));
                            newEquip.setMatk((short) (newEquip.getMatk() + mad));
                            // 교불
                            newEquip.setFlag(newEquip.getFlag() | ItemFlag.POSSIBLE_TRADING.getValue());

                            MapleInventoryManipulator.addbyItem(getClient(), newEquip);

                            self.say("성공했군. 자네의 장비에서 알 수 없는 기운이 느껴지네. …마치 그 악마처럼.");
                        }
                    } else {
                    	target.exchange(4036454, -removeQty);
                        self.say("…실패인가. 기운은 사라졌지만, 다행히 자네의 장비는 남았으니 돌려주겠네");
                    }
                }
                break;
            }
        }
    }

    public void Donation() {
        initNPC(MapleLifeFactory.getNPC(9000041));
            long totalMeso1 = PraiseDonationMeso.getTotalMeso(); 

            String v0 = "#fs11#안녕하세요 모험가 #b#h0##k님, #e강림월드#n에서 새로온 #b신입 뉴비#k들의 정착을 위한 #e#b뉴비 기부함#k#n을 운영하고 있습니다.\r\n"
                + "\r\n"
                + "#fs12##e[ 현재 기부된 금액 : #b" 
                + NumberFormat.getInstance().format(totalMeso1) 
                + " 메소 ]#k#n#fs11#\r\n"
                + "\r\n"
                + "#b#L0#뉴비 기부함이 무엇인지 좀 더 설명해주세요.#l\r\n"
                + "#L1#메소를 기부하겠습니다.#l\r\n"
                + "#L2#기부 랭킹을 보겠습니다.#l\r\n"
                + "#L3#기부 보상을 수령하겠습니다.#l\r\n"
                + "#L4#뉴비 지원금을 수령하겠습니다.#l\r\n"
                + "#L5#칭찬 상점을 이용하겠습니다.#l";        
                int v1 = self.askMenu(v0);
        switch (v1) {
            case 0: {
                String v2 = "#fs11#설명이 필요한 항목을 선택해주시기 바랍니다.\r\n\r\n";
                v2 += "#b#L0#메소 기부는 어떻게 하나요?#l\r\n";
                //v2 += "#b#L1#기부 랭킹 보상에 대해 설명해 주세요.#l\r\n";
                v2 += "#b#L2#기부 누적 보상에 대해 설명해 주세요.#l\r\n";
                v2 += "#b#L3#뉴비 지원금에 대해 설명해 주세요.#l\r\n";
                int v3 = self.askMenu(v2);
                switch (v3) {
                    case 0: {
                       self.say("#fs11#기부 메소 단위는 #b1억 메소#k로 #b1억 메소#k 당 #b100 칭찬 포인트#k를 획득할 수 있습니다.\r\n"
                               + "일일 최대 누적 가능 칭찬 포인트는 #r1,000,000으로 제한#k됩니다.\r\n\r\n"
                               + "#r※ 일일 제한 포인트를 채워도 랭킹에는 반영됩니다.");                        break;
                    }
                    case 1: {
                        self.say("#fs11#기부 랭킹은 #e매월 1일#n마다 아래 기준으로 보상을 지급합니다.\r\n\r\n#b1위 : 20만 강림 포인트 + 100만 메이플 포인트\r\n2위 : 15만 강림 포인트 + 70만 메이플 포인트\r\n3위~5위 : 10만 강림 포인트 + 50만 메이플 포인트\r\n6위~10위 : 5만 강림 포인트 + 30만 메이플 포인트#k\r\n\r\n매월 1일마다 뉴비 기부함에 누적된 메소가 #e1000억 메소#n를 넘길 경우 #e누적된 메소의 30%#n를 기부에 참여한 유저 중 30억 이상 기부자 #e랜덤 3명에게 1/n#n 하여 지급됩니다.\r\n\r\n#r(단, 메소가 과도하게 집중되는 문제를 방지하기 위해 30%의 최대 금액은 1500억으로 제한됩니다.)");
                        break;
                    }
                    case 2: {
                        self.say("#fs11#자신이 기부한 누적 메소를 기준으로 #e10억 메소#n 단위로 누적될 때마다 아래의 아이템을 지급받을 수 있습니다.\r\n\r\n#b#i4031227# #z4031227# 1개");
                        break;
                    }
                    case 3: {
                        self.say("#fs11#신규 유저는 #e계정 생성 후 3일이 지나기 전#n까지\r\n광장의 #b‘기부함’#k NPC를 통해 #e뉴비 지원금 500억 메소#n를\r\n수령할 수 있습니다.\r\n\r\n#b└ 뉴비 지원금으로 지급된 500억 메소는 뉴비 기부함 누적 메소에서 차감됩니다.\r\n\r\n└ 뉴비 지원금은 250레벨 이상 캐릭터만 수령 가능합니다.\r\n\r\n└ 뉴비 지원금은 유니온 8,000 이상 계정만 수령 가능합니다.\r\n");
                        break;
                    }
                }
                break;
            }
            case 1: {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                Calendar CAL = new GregorianCalendar(Locale.KOREA);
                String fDate = sdf.format(CAL.getTime());

                String[] dates = fDate.split("-");
                int year = Integer.parseInt(dates[0]);
                int month = Integer.parseInt(dates[1]);
                int day = Integer.parseInt(dates[2]);
                int hours = Integer.parseInt(dates[3]);
                int minutes = Integer.parseInt(dates[4]);


                if (day == 1 && hours == 00 && minutes >= 00 ||
                        day == 1 && hours == 00 && minutes < 5) { // 매달 1일 00시 00분부터 00시 04분까지 수령 불가
                    self.say("랭킹 집계로 인하여 00시 04분까지 기부에 참여할 수 없습니다.");
                    return;
                }

                Timestamp timestamp = getClient().getCreated();
                long delta = System.currentTimeMillis() - timestamp.getTime();

                if (delta < (1000 * 60 * 60 * 24 * 3L)) {
                    self.say("계정 생성 후 3일이 지난 계정만 기부에 참여할 수 있습니다.");
                    return;
                }

                long totalMeso = PraiseDonationMeso.getTotalMeso();
                PraiseDonationMesoRankEntry entry = PraiseDonationMesoRank.getRank(getPlayer().getAccountID());
                long myTotalMeso = 0;
                if (entry != null) {
                    myTotalMeso = entry.getTotalMeso();
                }
               String v2 = "#fs12##e#b<메소 기부>#k#n#fs11#\r\n\r\n"
                       + "메소 기부는 #b1억 메소#k 단위로 가능하며,\r\n#b1억 메소 당 100 칭찬 포인트#k를 획득할 수 있습니다.\r\n"
                       + "일일 최대 누적 가능 칭찬 포인트는 #r1,000,000으로 제한#k되며, 일일 제한 포인트를 넘어서 기부해도 랭킹에는 반영됩니다.\r\n\r\n";
                long meso = getPlayer().getMeso();
                int d = (int) (meso / 100000000);
                v2 += "현재 기부함 메소 : " + NumberFormat.getInstance().format(totalMeso) + " 메소#n\r\n";
                v2 += "#e이번 달 나의 기부 : " + NumberFormat.getInstance().format(myTotalMeso) + " 메소#n\r\n";
                v2 += "현재 기부 가능 메소 : " + NumberFormat.getInstance().format(d * 100000000L) + " 메소#n \r\n                                  #r(" + d + "회 가능)#k\r\n\r\n";
                v2 += "얼마나 기부하시겠습니까? #b(입력 수치당 = 1억 메소)#k\r\n\r\n";
                int v3 = self.askNumber(v2, 1, 1, d);
                if (v3 <= 0) {
                    self.say("기부할 수 있는 최소 메소 단위보다 가진 메소가 적어 기부를 진행할 수 없습니다.");
                    return;
                }
                long donationMeso = v3 * 100000000L;
                long donationMeso2 = v3;
                if (donationMeso > getPlayer().getMeso()) {
                    self.say("기부하려 시도한 메소보다 가진 메소가 적어 기부를 진행할 수 없습니다.");
                    return;
                }
                int max = getPlayer().getOneInfoQuestInteger(1211345, "today");
                int p = Math.min(1000000 - max, v3 * 100);
                if (1 == self.askYesNo("#fs11#정말 #e" + NumberFormat.getInstance().format(donationMeso) + " 메소#n를 기부하시겠습니까?\r\n기부 시 #r" + NumberFormat.getInstance().format(p) + " 칭찬 포인트#k를 획득할 수 있습니다.")) {
                    getPlayer().gainMeso(-donationMeso, true);

                    if (p > 0) {
                        PraisePoint point = getPlayer().getPraisePoint();
                        point.setTotalPoint(point.getTotalPoint() + (p));
                        point.setPoint(point.getPoint() + (p));

                        getPlayer().updateOneInfo(3887, "point", String.valueOf(point.getPoint()));
                        getPlayer().setSaveFlag(getPlayer().getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag());
                    }

                    getPlayer().updateOneInfo(1211345, "today", String.valueOf(max + p));

                    Center.Broadcast.broadcastMessage(CField.chatMsg(4, "[" + getPlayer().getName() + "]님이 뉴비 기부함에 '" + donationMeso2 + "억 메소'를 기부하셨습니다."));


                    PraiseDonationMeso.doDonationMeso(getPlayer(), getPlayer().getAccountID(), donationMeso);
                    self.say("#fs11##e" + NumberFormat.getInstance().format(donationMeso) + " 메소#n를 기부하여\r\n#r" + NumberFormat.getInstance().format(p) + " 칭찬 포인트를 획득하였습니다.");
                }
                break;
            }
            case 2: {
                List<PraiseDonationMesoRankEntry> ranks = PraiseDonationMesoRank.getTopRanker(); // 상위 50명
                String v2 = "#fs12##e<기부 랭킹 상위 50>#n\r\n\r\n";
                int count = 1;
                for (PraiseDonationMesoRankEntry r : ranks) {
                    if (count < 10) {
                        v2 = v2 + "#Cgray#00#b#e" + count + "위#n#k";
                    } else if (count >= 10 && count < 100) {
                        v2 = v2 + "#Cgray#0#b#e" + count + "위#n#k";
                    } else {
                        v2 = v2 + "#Cgray##b#e" + count + "위#n#k";
                    }
                    v2 = v2 + " " + r.getPlayerName() + " #e(메소 : " + NumberFormat.getInstance().format(r.getTotalMeso()) + ")#n\r\n";
                    count++;
                }
                self.say(v2);
                break;
            }
            case 3: {
                String v2 = "#fs11#어떤 보상을 수령하시겠습니까?\r\n\r\n";
                v2 += "#b#L0#누적 보상을 수령하겠습니다.#l\r\n";
                //v2 += "#L1#기부 랭킹 보상을 수령하겠습니다.#l";
                int v3 = self.askMenu(v2);
                switch (v3) {
                    case 0: {
                long totalMeso    = PraiseDonationMeso.getTotalMeso(getPlayer().getAccountID());
                int getCount      = getPlayer().getOneInfoQuestInteger(1211345, "get_reward");
                int count         = (int)(totalMeso / 1_000_000_000L);
                int remainCount   = count - getCount;
                String v4 = "#fs11##e<누적 보상 수령>#n\r\n#e총 누적 메소 : "
              + NumberFormat.getInstance().format(totalMeso) + "\r\n";
                v4 += "수령 가능 보상 횟수 : " + remainCount + " 회\r\n\r\n"
        + "#b#n누적 메소 10억 당 1회#k 보상 수령 가능합니다.\r\n\r\n";
            v4 += remainCount + "회를 모두 사용하여 아래의 아이템을 획득하시겠습니까?\r\n\r\n";
            v4 += "#b#i4031227# #z4031227# x" + remainCount;
            if (1 == self.askYesNo(v4)) {
                if (remainCount <= 0) {
                    self.say("수령 가능 횟수가 부족하여 보상을 수령할 수 없습니다.");
                    return;
                }
                if (target.exchange(4031227, remainCount) > 0) {
                    self.say("누적 보상 수령이 완료되었습니다.");
                    getPlayer().updateOneInfo(1211345, "get_reward",
                        String.valueOf(getCount + remainCount));
                } else {
                    self.say("#b인벤토리 슬롯을 확보하고 다시 시도해주시기 바랍니다.");
                }
            }
                        break;
                    }
                    case 1: {
                        // TODO: 랭킹 보상 수령, 1일 랭킹 정산, 1000억 넘을 시 30억 이상 기부 참여한 3명 추첨 1/n 수령 구현
                        int rank = getPlayer().getOneInfoQuestInteger(1234599, "praise_reward");
                        int get = getPlayer().getOneInfoQuestInteger(1234599, "praise_reward_get");
                        long meso = getPlayer().getOneInfoQuestLong(1234599, "praise_reward2");
                        int get2 = getPlayer().getOneInfoQuestInteger(1234599, "praise_reward2_get");

                        if ((rank > 10 || get > 0) && (meso <= 0 || get2 > 0)) {
                            self.say("기부 랭킹 보상 수령 가능 대상자가 아닙니다.");
                            return;
                        }
                        NumberFormat nf = NumberFormat.getInstance();
                        if (meso > 0 && get2 == 0) {
                            getPlayer().gainMeso(meso, true);
                            getPlayer().updateOneInfo(1234599, "praise_reward2", "0");
                            getPlayer().updateOneInfo(1234599, "praise_reward2_get", "1");

                            getPlayer().dropMessage(5, nf.format(meso) + "메소를 지급받았습니다.");
                        }
                        if (rank > 0 && get == 0) {
                            int realPoint = 0;
                            int maplePoint = 0;
                            if (rank == 1) {
                                realPoint = 200000;
                                maplePoint = 10000000;
                            } else if (rank == 2) {
                                realPoint = 150000;
                                maplePoint = 700000;
                            } else if (rank >= 3 && rank <= 5) {
                                realPoint = 100000;
                                maplePoint = 500000;
                            } else if (rank >= 6 && rank <= 10) {
                                realPoint = 50000;
                                maplePoint = 300000;
                            }
                            getPlayer().gainRealCash(realPoint, true);
                            getPlayer().modifyCSPoints(2, maplePoint, true);

                            getPlayer().updateOneInfo(1234599, "praise_reward", "0");
                            getPlayer().updateOneInfo(1234599, "praise_reward_get", "1");
                            
                            getPlayer().dropMessage(5, nf.format(realPoint) + " 강림 포인트와 " + nf.format(maplePoint) + " 메이플 포인트를 지급받았습니다.");
                        }
                        self.say("지급이 완료되었습니다.");
                        break;
                    }
                }
                break;
            }
            case 4: {
                Timestamp timestamp = getClient().getCreated();
                long delta = System.currentTimeMillis() - timestamp.getTime();

                if (delta >= (1000 * 60 * 60 * 24 * 3L)) {
                    self.say("계정 생성 후 3일 이전까지만 뉴비 지원금을 수령할 수 있습니다.");
                    return;
                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                Calendar CAL = new GregorianCalendar(Locale.KOREA);
                String fDate = sdf.format(CAL.getTime());

                String[] dates = fDate.split("-");
                int year = Integer.parseInt(dates[0]);
                int month = Integer.parseInt(dates[1]);
                int day = Integer.parseInt(dates[2]);
                int hours = Integer.parseInt(dates[3]);
                int minutes = Integer.parseInt(dates[4]);


                if (day == 1 && hours == 00 && minutes >= 00 ||
                        day == 1 && hours == 00 && minutes < 5) { // 매달 1일 00시 00분부터 00시 04분까지 수령 불가
                    self.say("랭킹 집계로 인하여 00시 04분까지 뉴비 지원금을 수령할 수 없습니다.");
                    return;
                }

                if (getPlayer().getLevel() < 250) {
                    self.say("250레벨 미만 캐릭터는 뉴비 지원금을 수령할 수 없습니다.");
                    return;
                }

                if (getPlayer().getUnionLevel() < 8000) {
                    self.say("유니온 레벨 8000 이상이어야 뉴비 지원금을 수령할 수 있습니다.");
                    return;
                }

                if (getPlayer().getOneInfoQuestInteger(1211345, "get_meso") > 0) {
                    self.say("금일에 이미 뉴비 지원금을 수령하였습니다.");
                    return;
                }

                long remainMeso = PraiseDonationMeso.getTotalMeso();
                long remainMeso2 = PraiseDonationMeso.getTotalMeso() - 500_0000_0000L;
                if (remainMeso < 500_0000_0000L) {
                    self.say("기부함에 남은 메소가 부족하여 뉴비 지원금을 수령할 수 없습니다.");
                    return;
                }

            PraiseDonationMeso.addTotalMeso(-500_0000_0000L);
             getPlayer().gainMeso(500_0000_0000L, true);
                getPlayer().updateOneInfo(1211345, "get_meso", "1");
                Center.Broadcast.broadcastMessage(CField.chatMsg(5, "[" + getPlayer().getName() + "]님이 뉴비 지원금 '500억 메소'를 획득하셨습니다!"));

                self.say("#fs11#뉴비 지원금 #e#b500억 메소#k#n를 수령하였습니다.\r\n\r\n#e기부함 남은 메소 : " + NumberFormat.getInstance().format(remainMeso2));
                break;
            }
                case 5: { // 칭찬 상점
                    int praise = getPlayer().getPraisePoint().getPoint();
                    StringBuilder menu = new StringBuilder();
                    menu.append("#fs14##e≪ 칭찬 포인트 상점 ≫#n\r\n\r\n")
                        .append("#fs12#내 칭찬 포인트: #b")
                        .append(NumberFormat.getInstance().format(praise))
                        .append("P #k\r\n")
                        .append("#fs11#─────────────────────────────\r\n");

                    int[] itemIDs = {4036660, 4036661, 5068306};   // 예시 아이템
                    int[] prices  = {100000, 500000, 1000000};   // 칭찬 포인트 단위

                    for (int i = 0; i < itemIDs.length; i++) {
                        menu.append("#fs11##L").append(i).append("#")
                            .append("#i").append(itemIDs[i]).append("# ")
                            .append("#z").append(itemIDs[i]).append("#    ")
                            .append("#fs11##b")
                            .append(NumberFormat.getInstance().format(prices[i]))
                            .append(" 포인트#k#l\r\n");
                    }

                    menu.append("#fs11#\r\n─────────────────────────────\r\n")
                        .append("#fs11#구매할 상품을 선택해 주세요.");

                    int sel = self.askMenu(menu.toString());
                    int cost = prices[sel], item = itemIDs[sel];

                    if (praise < cost) {
                        self.say("#fs12#칭찬 포인트가 부족합니다.");
                        break;
                    }

                    String confirm = new StringBuilder()
                        .append("#fs12#정말 #b")
                        .append(NumberFormat.getInstance().format(cost))
                        .append("#k 포인트를 사용하여\r\n")
                        .append("#fs12##i").append(item)
                        .append("# #z").append(item)
                        .append("##n\r\n")
                        .append("#fs12#을(를) 구매하시겠습니까?")
                        .toString();

                    if (self.askYesNo(confirm) == 1) {
                        // 포인트 차감
                        PraisePoint pt = getPlayer().getPraisePoint();
                        pt.setPoint(pt.getPoint() - cost);
                        pt.setTotalPoint(pt.getTotalPoint() - cost);
                        getPlayer().updateOneInfo(3887, "point", String.valueOf(pt.getPoint()));
                        getPlayer().setSaveFlag(
                            getPlayer().getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag()
                        );
                        // 아이템 지급
                        if (target.exchange(item, 1) > 0) {
                            self.say("#fs12#구매가 완료되었습니다!");
                        } else {
                            self.say("#fs12#인벤토리에 공간이 부족합니다.");
                        }
                    }
                    break;
                }
        }
    }

    public void buyStone(long price, int count, int type, int rate) {
        String payType = "강림 포인트";
        long getPrice = getPlayer().getRealCash();
        if (type == 1) {
            payType = "홍보 포인트";
            getPrice = getPlayer().getHongboPoint();
        } else if (type == 2) {
            payType = "메소";
            getPrice = getPlayer().getMeso();
        } else if (type == 3) {
            payType = "강림 캐시";
            getPrice = getPlayer().getCashPoint();
        } else if (type == 4) {
            payType = "찬란한 빛의 결정";
            getPrice = getPlayer().getItemQuantity(4031227, false);
        }

        NumberFormat nf = NumberFormat.getInstance();
        String payText = nf.format(price) + " " + payType;
        String v0 = "";
        if (DBConfig.isGanglim) {
            v0 += "#fs11#";
        }
        v0 += "#b" + nf.format(price) + " " + payType + "#k를 사용하여 각인석을 꺼내겠어?\r\n어떤 각인석이 꺼내질지는 꺼내봐야 알아.\r\n\r\n#e보유 중인 " + payType + " : " + NumberFormat.getInstance().format(getPrice);
        if (1 == self.askYesNo(v0)) {
            if (type == 0) {
                if (getPlayer().getRealCash() < price) {
                    self.say(payType + "가 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            } else if (type == 1) {
                if (getPlayer().getHongboPoint() < price) {
                    self.say(payType + "가 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            } else if (type == 2) {
                if (getPlayer().getMeso() < price) {
                    self.say(payType + "가 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            } else if (type == 3) {
                if (getPlayer().getCashPoint() < price) {
                    self.say("#fs11#" + payType + "가 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            } else if (type == 4) {
                if (getPlayer().getItemQuantity(4031227, false) < price) {
                    self.say("#fs11#" + payType + "이 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            }
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < count) {
                self.say("#b소비 인벤토리#k 슬롯을 " + count + "칸 이상 확보하고 다시 시도해줘.");
                return;
            }
            if (type == 0) {
                getPlayer().gainRealCash((int) -price, true);
            } else if (type == 1) {
                getPlayer().gainHongboPoint((int) -price, true);
            } else if (type == 2) {
                getPlayer().gainMeso(-price, true);
            } else if (type == 3) {
                getPlayer().gainCashPoint((int) -price);
            } else if (type == 4) {
                if (target.exchange(4031227, (int)-price) < 0) {
                    self.say("#fs11# " + payType + "이 부족한 거 같은데? 아무리 너라도 고용비는 확실해야 하는 법이지.");
                    return;
                }
            }

            int l1 = 0;
            int l2 = 0;
            for (int i = 0; i < count; ++i) {
                int itemID = 2432127;
                if (Randomizer.isSuccess(rate)) {
                    itemID = 2432126;
                    l2++;
                } else {
                    l1++;
                }
                if (target.exchange(itemID, 1) > 0) {
                }
            }

            String list = "";
            String list2 = "";
            if (l1 > 0) {
                list += "#i2432127# #z2432127# x" + l1;
                list2 += "2432127 " + l1 + "개";
            }
            if (l2 > 0) {
                if (l1 > 0) {
                    list += "\r\n";
                    list2 += "\r\n";
                }
                list += "#i2432126# #z2432126# x" + l2;
                list2 += "2432126 " + l2 + "개";
            }

            StringBuilder sb = new StringBuilder("각인석 뽑기 결과 (뽑은 아이템 : " + list + ", 사용재화 : " + payType + ", 사용자 : " + getPlayer().getName() + ")");
            LoggingManager.putLog(new ConsumeLog(getPlayer(), 1, sb));

            //objects.utils.FileoutputUtil.log("./TextLog/BuyStoneCraft.txt", "각인석 뽑기 (아이템ID : " + itemID + ", 지불 방식 : 후원, 사용자 : " + getPlayer().getName() + ")\r\n");
            String v7 = "";
            if (DBConfig.isGanglim) {
                v7 += "#fs11#";
            }
            v7 += "#b" + list + "#k\r\n꺼내온 각인석들이야.\r\n\r\n한 번 더 가지고 올까?\r\n원한다면 10개씩 가지고 올 수도 있어.\r\n\r\n#b#L0#";
            if (type == 2) {
                v7 += "1,500,000,000메소를 지불하여 각인석을 1개 획득합니다.#l\r\n#L1#15,000,000,000메소를 지불하여 각인석을 10개 획득합니다.#l";
            } else if (type == 0 || type == 1 || type == 3) {
                v7 += "3,000 " + payType + "를 지불하여 각인석을 1개 획득합니다.#l\r\n#L1#30,000 " + payType + "를 지불하여 각인석을 10개 획득합니다.#l";
            } else {
                v7 += payType + " 600개를 지불하여 각인석을 1개 획득합니다.#l\r\n#L1#" + payType + " 6000개를 지불하여 각인석을 10개 획득합니다.#l";
            }
            
            int v6 = self.askMenu(v7);
            int c = 1;
            if (v6 == 1) {
                c = 10;
            }
 long p;
 if (type == 2) {
     // 메소
     p = 1500000000L * c;
 } else if (type == 4) {
     // 찬란한 빛의 결정
     p = 600L * c;
 } else {
     // 홍보 포인트, 강림 캐시
     p = 3000L * c;
 }
            buyStone(p, c, type, rate);
            return;
        }
    }

    public void spinOff_UIOpen() {
        initNPC(MapleLifeFactory.getNPC(1530021));
        String v0 = "";
        if (DBConfig.isGanglim) { // 각인석판이 없을경우 1회 지급
            if (getPlayer().getItemQuantity(2432128, false) < 1) {
                getPlayer().gainItem(2432128, 1);
            }
        }
        if (DBConfig.isGanglim) {
            v0 += "#fs11#";
        }
        v0 += "#h0#! 오랜만이야!\r\n스승님께서 검은 마법사가 남긴 기록을 연구하시다가 이상한 돌을 발견하셨어.\r\n\r\n#b#i2432126# #z2432126#\r\n #i2432127#  #z2432127##k\n\r\n\r\n이게 바로 그 돌이야.";

        self.say(v0);

        String v1 = "";
        if (DBConfig.isGanglim) {
            v1 += "#fs11#";
        }
        v1 += "스승님께서는 #b“겉으로는 평범해 보이는 돌이지만 미약하게 검은 마법사의 기운이 느껴진다. 불안정한 기운을 다듬으면 힘의 일부를 사용할 수 있을지도 모른다. 하지만……”#k 이렇게 중얼거리시더니 갑자기 어디론가 사라지셨어.\r\n\r\n어때? 관심 있어? (말하고 싶어 하는 눈치다. 한 번 들어 보기로 할까.)\r\n\r\n";
        v1 += "#b#L0#검은 마법사가 남긴 돌?#l\r\n#L1#각인 석판을 확인하고 싶어.#l\r\n#L2#포인트를 사용하여 각인석을 뽑고 싶어.#l\r\n#L3#포인트를 사용하여 각인석을 세공하고 싶어.#l";
        int v2 = self.askMenu(v1);
        switch (v2) {
            case 0: {
                String v3 = "";
                if (DBConfig.isGanglim) {
                    v3 += "#fs11#";
                }
                v3 += "스승님께서는 이 돌을 각인석이라고 부르셨어.\r\n\r\n#b(엘윈은 들고 있는 석판에 쌓인 먼지를 털어내더니 내게 건넸다. 각인석 하나가 겨우 들어갈 크기의 홈이 석판의 군데군데 파여 있다.)#k\r\n\r\n이 돌의 불안정한 기운을 다듬는 작업을 세공이라고 하는데, 세공을 마친 각인석을 석판의 홈에 끼우면 돌에 남아 있는 힘의 일부를 사용할 수 있을 거야. 나도 스승님께서 하시는 말씀을 듣기만 한 거라서 정확하게는 모르지만…….\r\n\r\n";
                v3 += "#b#L0#어디서 얻을 수 있지?#l\r\n#L1#세공하는 방법은?#l";
                int v4 = self.askMenu(v3);
                switch (v4) {
                    case 0: {
                        String v5 = "";
                        if (DBConfig.isGanglim) {
                            v5 = "#fs11#각인석은 검은 마법사의 지하 서고에 있는 보관함에 들어 있어.\r\n결계 때문에 스승님이나 내가 없으면 꺼낼 수 없으니까 필요하면 나한테 말해.\r\n\r\n#h0# 너라고 해도, 결계 마법사 고용비는 제대로 받을 거야.\r\n보관함에서 어떤 각인석이 꺼내질지는 나도 몰라. 어떤 게 나오더라도 고용비는 똑같으니까 알아둬.\r\n\r\n";
                            v5 += "#e강림 캐시 : 3,000 캐시\r\n#b[정밀한 각인석 80%, 평범한 각인석 20%]#k\r\n찬란한 빛의 결정 : 500 개\r\n#b[정밀한 각인석 10%, 평범한 각인석 90%]#k#k";
                        } else {
                            v5 = "각인석은 검은 마법사의 지하 서고에 있는 보관함에 들어 있어.\r\n결계 때문에 스승님이나 내가 없으면 꺼낼 수 없으니까 필요하면 나한테 말해.\r\n\r\n#h0# 너라고 해도, 결계 마법사 고용비는 제대로 받을 거야.\r\n보관함에서 어떤 각인석이 꺼내질지는 나도 몰라. 어떤 게 나오더라도 고용비는 똑같으니까 알아둬.\r\n\r\n";
                            v5 += "#e강림 포인트 : 3,000 #b(정밀한 각인석 80%, 평범한 각인석 20%)#k\r\n홍보 포인트 : 3,000 #b(정밀한 각인석 60%, 평범한 각인석 40%)#k\r\n메소 : 1,500,000,000 #b(정밀한 각인석 10%, 평범한 각인석 90%)#k";
                        }
                        self.say(v5);

                        /*String v6 = "그러고 보니 검은 마법사에게 영향을 받은 군단장의 잔상을 처치하면 각인석을 얻을 수 있을지도 몰라 #r(스우, 데미안, 루시드, 윌, 더스크, 듄켈, 진 힐라, 검은 마법사 처치 시 확률적으로 하급 각인석 상자 또는 중급 각인석 상자를 드롭할 수 있습니다).#k";
                        v6 += "\r\n\r\n#e30% 확률로 ‘하급 각인석 상자’ 드롭(개인 드롭, 최대 1개)#n\r\n#b└ 정밀한 각인석 10%, 평범한 각인석 90%#k\r\n\r\n#e10% 확률로 ‘중급 각인석 상자’ 드롭(개인 드롭, 최대 1개)#n\r\n#b└ 정밀한 각인석 30%, 평범한 각인석 70%#k";
                        self.say(v6);*/
                        break;
                    }
                    case 1: {
                        String v5 = "";
                        if (DBConfig.isGanglim) {
                            v5 += "#fs11#";
                        }
                        v5 += "스승님 허락 없이 저희끼리 이래도 괜찮은 걸까요……?\r\n\r\n각인석을 저한테 가지고 오시면 불안정한 기운을 다듬어 드릴게요.\r\n각인석에 남아 있는 기운이 불안정해서 세공에 실패할 수도 있어요.\r\n\r\n";
                        self.sayReplacedNpc(v5, 1530040);

                        String v6 = "";
                        if (DBConfig.isGanglim) {
                            v6 = "#fs11##h0# 이라고 해도, 조작 마법사 고용비는 제대로 받을 거예요. 세공에 실패하더라도 고용비는 똑같으니까 알고 계세요.\r\n\r\n#e강림 캐시 : 1,000 캐시\r\n강림 포인트 : 3,000 포인트\r\n찬란한 빛의 결정 : 200 개";
                        } else {
                            v6 = "#h0# 이라고 해도, 조작 마법사 고용비는 제대로 받을 거예요. 세공에 실패하더라도 고용비는 똑같으니까 알고 계세요.\r\n\r\n#e강림 포인트 : 1,000\r\n홍보 포인트 : 1,000\r\n메소 : 500,000,000";
                        }
                        self.sayReplacedNpc(v6, 1530040);

                        String v7 = "";
                        if (DBConfig.isGanglim) {
                            v7 += "#fs11#";
                        }
                        v7 += "#r※ 장착한 각인석은 임의로 해제할 수 없으며, 다른 각인석을 장착하면 기존 각인석을 파괴하고 그 자리에 장착하게 됩니다. 파괴된 각인석은 흔적이 남지 않으며 회수가 불가합니다.";
                        v7 += "\r\n\r\n※ 각인석은 플러스 10회, 마이너스 10회 총 20회의 세공을 모두 마쳐야 옵션이 적용됩니다.\r\n\r\n";
                        self.sayReplacedNpc(v7, 1530040);
                        break;
                    }
                }
                break;
            }
            case 1: {
                String empty = "#fc0xFF6600CC##fUI/UIWindow.img/IconBase/0#";
                String icon = "#fs11##fc0xFF6600CC##i";
                String v3 = "";
                if (DBConfig.isGanglim) {
                    v3 += "#fs11#";
                }
                v3 += "#e[석판에 장착된 각인석]#n\r\n\r\n";
                int item1 = getPlayer().getOneInfoQuestInteger(133333, "equip1");
                int item2 = getPlayer().getOneInfoQuestInteger(133333, "equip2");
                int item3 = getPlayer().getOneInfoQuestInteger(133333, "equip3");
                int item4 = getPlayer().getOneInfoQuestInteger(133333, "equip4");
                int item5 = getPlayer().getOneInfoQuestInteger(133333, "equip5");

                v3 += item1 > 0 ? (icon + item1 + "# ") : (empty + " ");
                v3 += item2 > 0 ? (icon + item2 + "# ") : (empty + " ");
                v3 += item3 > 0 ? (icon + item3 + "# ") : (empty + " ");
                v3 += item4 > 0 ? (icon + item4 + "# ") : (empty + " ");
                v3 += item5 > 0 ? (icon + item5 + "# ") : (empty + " ");

                int plusCount1 = getPlayer().getOneInfoQuestInteger(133333, "craftPlus1");
                int plusCount2 = getPlayer().getOneInfoQuestInteger(133333, "craftPlus2");
                int plusCount3 = getPlayer().getOneInfoQuestInteger(133333, "craftPlus3");
                int plusCount4 = getPlayer().getOneInfoQuestInteger(133333, "craftPlus4");
                int plusCount5 = getPlayer().getOneInfoQuestInteger(133333, "craftPlus5");

                int minusCount1 = getPlayer().getOneInfoQuestInteger(133333, "craftMinus1");
                int minusCount2 = getPlayer().getOneInfoQuestInteger(133333, "craftMinus2");
                int minusCount3 = getPlayer().getOneInfoQuestInteger(133333, "craftMinus3");
                int minusCount4 = getPlayer().getOneInfoQuestInteger(133333, "craftMinus4");
                int minusCount5 = getPlayer().getOneInfoQuestInteger(133333, "craftMinus5");

                int optionPlus1 = getPlayer().getOneInfoQuestInteger(133333, "plusValue1");
                int optionPlus2 = getPlayer().getOneInfoQuestInteger(133333, "plusValue2");
                int optionPlus3 = getPlayer().getOneInfoQuestInteger(133333, "plusValue3");
                int optionPlus4 = getPlayer().getOneInfoQuestInteger(133333, "plusValue4");
                int optionPlus5 = getPlayer().getOneInfoQuestInteger(133333, "plusValue5");


                int optionMinus1 = getPlayer().getOneInfoQuestInteger(133333, "minusValue1");
                int optionMinus2 = getPlayer().getOneInfoQuestInteger(133333, "minusValue2");
                int optionMinus3 = getPlayer().getOneInfoQuestInteger(133333, "minusValue3");
                int optionMinus4 = getPlayer().getOneInfoQuestInteger(133333, "minusValue4");
                int optionMinus5 = getPlayer().getOneInfoQuestInteger(133333, "minusValue5");

                String lock1 = getPlayer().getOneInfoQuestInteger(133333, "lock1") > 0 ? "#r(잠김)" : "#b(열림)";
                String lock2 = getPlayer().getOneInfoQuestInteger(133333, "lock2") > 0 ? "#r(잠김)" : "#b(열림)";
                String lock3 = getPlayer().getOneInfoQuestInteger(133333, "lock3") > 0 ? "#r(잠김)" : "#b(열림)";
                String lock4 = getPlayer().getOneInfoQuestInteger(133333, "lock4") > 0 ? "#r(잠김)" : "#b(열림)";
                String lock5 = getPlayer().getOneInfoQuestInteger(133333, "lock5") > 0 ? "#r(잠김)" : "#b(열림)";

                int unlock1 = getPlayer().getOneInfoQuestInteger(133333, "unlock1");
                int unlock2 = getPlayer().getOneInfoQuestInteger(133333, "unlock2");

                v3 += "\r\n" + lock1 + " " + lock2 + " " + lock3 + " " + lock4 + " " + lock5;
                v3 += "\r\n\r\n#k";
                String item = item1 > 0 ? ("#b#z" + item1 + "##k") : "#r장착되지 않음#k";
                String ImprintedCount = (plusCount1 + minusCount1) == 20 ? "#b세공완료#k" : "#b[" + plusCount1 + "/10] #r[" + minusCount1 + "/10]#k";
                String ImprintedEnabled = (plusCount1 + minusCount1) == 20 ? "#fc0xFF9d1ffe#적용#k" : "#fc0xFFc983ff#미적용#k";
                ImprintedStoneOption plusOption1 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "plusOption1"));
                ImprintedStoneOption minusOption1 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "minusOption1"));
                v3 += "1번째 홈 : " + item + " | " + ImprintedCount + "\r\n";
                v3 += "ㄴ #b[" + String.format(plusOption1.getDesc(), "+", optionPlus1, "%", "") + ", #r" + String.format(minusOption1.getDesc(), "-", optionMinus1, "%", "") + "]#k | " + ImprintedEnabled + "\r\n";

                item = item2 > 0 ? ("#b#z" + item2 + "##k") : "#r장착되지 않음#k";
                ImprintedCount = (plusCount2 + minusCount2) == 20 ? "#b세공완료" : "#b[" + plusCount2 + "/10] #r[" + minusCount2 + "/10]#k";
                ImprintedEnabled = (plusCount2 + minusCount2) == 20 ? "#fc0xFF9d1ffe#적용#k" : "#fc0xFFc983ff#미적용#k";
                ImprintedStoneOption plusOption2 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "plusOption2"));
                ImprintedStoneOption minusOption2 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "minusOption2"));
                v3 += "2번째 홈 : " + item + " | " + ImprintedCount + "\r\n";
                v3 += "ㄴ #b[" + String.format(plusOption2.getDesc(), "+", optionPlus2, "%", "") + ", #r" + String.format(minusOption2.getDesc(), "-", optionMinus2, "%", "") + "]#k | " + ImprintedEnabled + "\r\n";

                item = item3 > 0 ? ("#b#z" + item3 + "##k") : "#r장착되지 않음#k";
                ImprintedCount = (plusCount3 + minusCount3) == 20 ? "#b세공완료" : "#b[" + plusCount3 + "/10] #r[" + minusCount3 + "/10]#k";
                ImprintedEnabled = (plusCount3 + minusCount3) == 20 ? "#fc0xFF9d1ffe#적용#k" : "#fc0xFFc983ff#미적용#k";
                ImprintedStoneOption plusOption3 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "plusOption3"));
                ImprintedStoneOption minusOption3 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "minusOption3"));
                v3 += "3번째 홈 : " + item + " | " + ImprintedCount + "\r\n";
                v3 += "ㄴ #b[" + String.format(plusOption3.getDesc(), "+", optionPlus3, "%", "") + ", #r" + String.format(minusOption3.getDesc(), "-", optionMinus3, "%", "") + "]#k | " + ImprintedEnabled + "\r\n";

                item = item4 > 0 ? ("#b#z" + item4 + "##k") : "#r장착되지 않음#k";
                ImprintedCount = (plusCount4 + minusCount4) == 20 ? "#b세공완료" : "#b[" + plusCount4 + "/10] #r[" + minusCount4 + "/10]#k";
                ImprintedEnabled = (plusCount4 + minusCount4) == 20 ? "#fc0xFF9d1ffe#적용#k" : "#fc0xFFc983ff#미적용#k";
                ImprintedStoneOption plusOption4 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "plusOption4"));
                ImprintedStoneOption minusOption4 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "minusOption4"));
                v3 += "4번째 홈 : " + item + " | " + ImprintedCount + "\r\n";
                v3 += "ㄴ #b[" + String.format(plusOption4.getDesc(), "+", optionPlus4, "%", "") + ", #r" + String.format(minusOption4.getDesc(), "-", optionMinus4, "%", "") + "]#k | " + ImprintedEnabled + "\r\n";

                /*if (unlock1 == 0) {
                    v3 += " #e(봉인됨)#n";
                }
                v3 += "\r\n";*/
                item = item5 > 0 ? ("#b#z" + item5 + "##k") : "#r장착되지 않음#k";
                ImprintedCount = (plusCount5 + minusCount5) == 20 ? "#b세공완료" : "#b[" + plusCount5 + "/10] #r[" + minusCount5 + "/10]#k";
                ImprintedEnabled = (plusCount5 + minusCount5) == 20 ? "#fc0xFF9d1ffe#적용#k" : "#fc0xFFc983ff#미적용#k";
                ImprintedStoneOption plusOption5 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "plusOption5"));
                ImprintedStoneOption minusOption5 = ImprintedStoneOption.getByOption(getPlayer().getOneInfoQuestInteger(133333, "minusOption5"));
                v3 += "5번째 홈 : " + item + " | " + ImprintedCount + "\r\n";
                v3 += "ㄴ #b[" + String.format(plusOption5.getDesc(), "+", optionPlus5, "%", "") + ", #r" + String.format(minusOption5.getDesc(), "-", optionMinus5, "%", "") + "]#k | " + ImprintedEnabled + "\r\n";
                /*if (unlock2 == 0) {
                    v3 += " #e(봉인됨)#n";
                }
                v3 += "\r\n";*/
                //v3 += "\r\n#b#L0#각인 석판을 인벤토리에 집어 넣습니다.#l";
                v3 += "\r\n";
                v3 += "\r\n#b#L1#1번째 석판의 홈을 잠금 설정을 변경하겠습니다.#l";
                v3 += "\r\n#b#L2#2번째 석판의 홈을 잠금 설정을 변경하겠습니다.#l";
                v3 += "\r\n#b#L3#3번째 석판의 홈을 잠금 설정을 변경하겠습니다.#l";
                v3 += "\r\n#b#L4#4번째 석판의 홈을 잠금 설정을 변경하겠습니다.#l";
                v3 += "\r\n#b#L5#5번째 석판의 홈을 잠금 설정을 변경하겠습니다.#l";


                int v4 = self.askMenu(v3);
                switch (v4) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5: {
                        if (getPlayer().getOneInfoQuestInteger(133333, "lock" + v4) > 0) {
                            getPlayer().updateOneInfo(133333, "lock" + v4, "0");
                            if (DBConfig.isGanglim) {
                                self.say("#fs11##b" + (v4) + "번째 홈의 잠금이 해제 되었습니다");
                            } else {
                                self.say("#b" + (v4) + "번째 홈의 잠금이 해제 되었습니다");
                            }

                        } else {
                            getPlayer().updateOneInfo(133333, "lock" + v4, "1");
                            if (DBConfig.isGanglim) {
                                self.say("#fs11##b" + (v4) + "번째 홈의 잠금이 설정 되었습니다");
                            } else {
                                self.say("#b" + (v4) + "번째 홈의 잠금이 해제 되었습니다");
                            }
                        }
                        break;
                    }
                }
                break;
            }
            
            case 2: {
                String v3 = "";
                if (DBConfig.isGanglim) {
                    v3 += "#fs11#";
                    v3 += "각인석이 더 필요해? 검은 마법사의 지하 서고에서 가지고 올게.\r\n\r\n고용비는 어떻게 지불할래?\r\n\r\n#b";
                    v3 += "#L4#강림 3,000 캐시를 지불하여 각인석을 획득합니다.#l\r\n";
                    v3 += "#L5#찬란한 빛의 결정 600개를 지불하여 각인석을 획득합니다.#l\r\n";
                    v3 += "#L3#조금만 더 고민해 볼게.#l";
                } else {
                    v3 += "각인석이 더 필요해? 검은 마법사의 지하 서고에서 가지고 올게.\r\n\r\n고용비는 어떻게 지불할래?\r\n\r\n#b";
                    v3 += "#L0#강림 포인트 3,000을 지불하여 각인석을 획득합니다.#l\r\n";
                    v3 += "#L1#홍보 포인트 3,000을 지불하여 각인석을 획득합니다.#l\r\n";
                    v3 += "#L2#메소 1,500,000,000을 지불하여 각인석을 획득합니다.#l\r\n";
                    v3 += "#L3#조금만 더 고민해 볼게.#l";
                }
                int v4 = self.askMenu(v3);
                switch (v4) {
                    case 0: {
                        buyStone(3000, 1, 0, 80);
                        break;
                    }
                    case 1: {
                        buyStone(3000, 1, 1, 60);
                        break;
                    }
                    case 2: {
                        buyStone(1500000000, 1, 2, 10);
                        break;
                    }
                    case 4: {
                        buyStone(3000, 1, 3, 80);
                        break;
                    }
                    case 5: {
                        buyStone(600, 1, 4, 10);
                        break;
                    }
                }
                break;
            }
            case 3: {
                initNPC(MapleLifeFactory.getNPC(1530040));
                String v3 = "";
                if (DBConfig.isGanglim) {
                    v3 += "#fs11#";
                }
                v3 += "세공은 각인 석판에 끼워져 있는 각인석에만 가능해요.\r\n각인석 세공 시 증감하는 옵션은 최초 세공 시 랜덤으로 정해지는데, 석판에 끼워져 있는 각인석이 무엇인지에 따라 정해지는 옵션의 종류가 달라질 수 있어요.\r\n\r\n";
                v3 += "#e아래 옵션 중에서 플러스 옵션 1종#n\r\n#b└ 정밀한 각인석 : 올스탯 5%, 공격력 2%, 마력 2%, 크리티컬 데미지 2%, 보스 공격력 5%, 몬스터 방어력 무시 5%, 크리티컬 확률 5%, 최종 데미지 2%\r\n└ 평범한 각인석 : 올스탯 2%, 보스 공격력 2%, 몬스터 방어력 무시 2%, 크리티컬 확률 2%\r\n\r\n#k";
                v3 += "#e아래 옵션 중에서 마이너스 옵션 1종#n\r\n#b└ 정밀한 각인석 : 데미지 2% 감소 (플러스 옵션이 최종 데미지 %인 경우, 최종 데미지 2% 감소 확정)\r\n평범한 각인석 : 데미지 1% 감소, 올스탯 1% 감소#k\r\n\r\n";
                self.sayReplacedNpc(v3, 1530040);

                String v4 = "";
                if (DBConfig.isGanglim) {
                    v4 += "#fs11#";
                    v4 += "각인석은 #e플러스 10회 마이너스 10회 총 20회#n의 세공이 가능해요. 기운이 불안정해서 세공이 실패할 수도 있다는 점 알고 계세요!\r\n\r\n#r세공을 시도하면 성공 여부와는 무관하게 세공 가능 횟수가 차감되고 복구할 수 없어요.#k\r\n\r\n#b";
                    v4 += "#L4#강림 1,000 캐시를 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L6#강림 3,000 포인트를 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L5#찬란한 빛의 결정 200개를 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L99#조금만 더 고민해 볼게.#l\r\n";
                } else {
                    v4 += "각인석은 #e플러스 10회 마이너스 10회 총 20회#n의 세공이 가능해요. 기운이 불안정해서 세공이 실패할 수도 있다는 점 알고 계세요!\r\n\r\n#r세공을 시도하면 성공 여부와는 무관하게 세공 가능 횟수가 차감되고 복구할 수 없어요.#k\r\n\r\n#b";
                    v4 += "#L0#강림 포인트 1,000을 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L1#홍보 포인트 1,000을 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L2#메소 500,000,000을 지불하여 각인석을 세공합니다.#l\r\n";
                    v4 += "#L3#조금만 더 고민해 볼게.#l\r\n";
                }

                int v5 = self.askMenu(v4);
                doCraft(ImprintedStonePayType.getType(v5));
                break;
            }
        }
    }

    public void doCraft(ImprintedStonePayType payType) {
        String v6 = "";
        if (payType == null) {
            return;
        }
        if (DBConfig.isGanglim) {
            v6 += "#fs11#";    
        }
        v6 += "세공을 원하는 홈을 선택해주세요. 선택한 홈에 장착된 각인석의 세공이 진행됩니다.";
        //int unlock1 = getPlayer().getOneInfoQuestInteger(133333, "unlock1");
        //int unlock2 = getPlayer().getOneInfoQuestInteger(133333, "unlock2");

        v6 += "\r\n\r\n#b#L0#1번째 홈에 각인석을 세공할게.#l\r\n";
        v6 += "#b#L1#2번째 홈에 각인석을 세공할게.#l\r\n";
        v6 += "#b#L2#3번째 홈에 각인석을 세공할게.#l\r\n";
        //if (unlock1 == 1) {
            v6 += "#b#L3#4번째 홈에 각인석을 세공할게.#l\r\n";
        //}
        //if (unlock2 == 1) {
            v6 += "#b#L4#5번째 홈에 각인석을 세공할게.#l\r\n";
        //}

        int v7 = self.askMenu(v6);

        int index = v7 + 1;

        int itemID = getPlayer().getOneInfoQuestInteger(133333, "equip" + index);

        if (itemID == 0) {
            self.say("#e" + index + "홈#n에 장착된 각인석이 없는 걸요? 다시 확인해주시고 시도해주세요!");
            return;
        }
        int remainCraftPlus = 10 - getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index);
        int remainCraftMinus = 10 - getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index);
        int plusOption_ = getPlayer().getOneInfoQuestInteger(133333, "plusOption" + index);
        int minusOption_ = getPlayer().getOneInfoQuestInteger(133333, "minusOption" + index);
        int plusValue = getPlayer().getOneInfoQuestInteger(133333, "plusValue" + index);
        int minusValue = getPlayer().getOneInfoQuestInteger(133333, "minusValue" + index);

        ImprintedStoneOption plusOption = ImprintedStoneOption.getByOption(plusOption_);
        ImprintedStoneOption minusOption = ImprintedStoneOption.getByOption(minusOption_);


        String v8 = "";
        if (DBConfig.isGanglim) {
            v8 += "#fs11#";
        }
        v8 += "#e[" + index + "번째 홈]#n\r\n";
        v8 += "#b#i" + itemID + "# #z" + itemID + "##k\r\n\r\n";
        v8 += "#e남은 플러스 세공 횟수 : " + remainCraftPlus + "\r\n";
        v8 += "#e남은 마이너스 세공 횟수 : " + remainCraftMinus + "\r\n";
        v8 += "#b플러스 옵션 : " + String.format(plusOption.getDesc(), "+", plusValue, "%", "증가") + "\r\n";
        v8 += ("#r마이너스 옵션 : " + String.format(minusOption.getDesc(), "-", minusValue, "%", "감소") + "\r\n");
        if (payType == ImprintedStonePayType.Donation) {
            v8 += "현재 보유중인 강림 포인트 : " + NumberFormat.getInstance().format(getPlayer().getRealCash());
        } else if (payType == ImprintedStonePayType.Promotion) {
            v8 += "현재 보유중인 홍보 포인트 : " + NumberFormat.getInstance().format(getPlayer().getHongboPoint());
        } else if (payType == ImprintedStonePayType.Meso) {
            v8 += "현재 보유중인 메소 : " + NumberFormat.getInstance().format(getPlayer().getMeso());
        } else if (payType == ImprintedStonePayType.RoyalCash) {
            v8 += "#k#e현재 보유중인 캐시 : " + NumberFormat.getInstance().format(getPlayer().getCashPoint());
        } else if (payType == ImprintedStonePayType.RoyalDPoint) {
            v8 += "#k#e현재 보유중인 포인트 : " + NumberFormat.getInstance().format(getPlayer().getDonationPoint());
        } else if (payType == ImprintedStonePayType.RoyalRedBall) {
            v8 += "#k#e현재 보유중인 찬란한 빛의 결정 : " + getPlayer().getItemQuantity(4031227, false);
        }
        v8 += "\r\n\r\n#k#n플러스, 마이너스 중 어떤 것을 세공하시겠어요?\r\n";
        v8 += "#b#L0#플러스 세공을 진행할게.#l\r\n";
        v8 += "#b#L1#마이너스 세공을 진행할게.#l\r\n";
        v8 += "#b#L2#좀 더 생각해볼게#l\r\n";
        int v9 = self.askMenu(v8);
        switch (v9) {
            case 0: {
                doPlusCraft(itemID, index, payType);
                break;
            }
            case 1: {
                doMinusCraft(itemID, index, payType);
                break;
            }
        }
    }

    public void doMinusCraft(int itemID, int index, ImprintedStonePayType payType) {
        String payMessage = "1,000 강림 포인트";
        if (payType == ImprintedStonePayType.Promotion) {
            payMessage = "1,000 홍보 포인트";
        } else if (payType == ImprintedStonePayType.Meso) {
            payMessage = "500,000,000 메소";
        } else if (payType == ImprintedStonePayType.RoyalCash) {
            payMessage = "1,000 강림 캐시";
        } else if (payType == ImprintedStonePayType.RoyalDPoint) {
            payMessage = "3,000 강림 포인트";
        } else if (payType == ImprintedStonePayType.RoyalRedBall) {
            payMessage = "찬란한 빛의 결정 200개";
        }
        if (DBConfig.isGanglim) {
            if (0 == self.askYesNo("#fs11#정말 #e" + payMessage + "#n를 사용하여 세공을 할 건가요?")) {
                return;
            }
        } else {
            if (0 == self.askYesNo("정말 #e" + payMessage + "#n를 사용하여 세공을 할 건가요?")) {
                return;
            }
        }

        int craftCount = getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index);
        if (craftCount >= 10) {
            if (DBConfig.isGanglim) {
                self.say("#fs11#이미 마이너스 세공을 완료한 각인석인 거 같네요.");    
            } else {
                self.say("이미 마이너스 세공을 완료한 각인석인 거 같네요.");
            }
            return;
        }
        if (payType == ImprintedStonePayType.Donation.getType()) {
            if (getPlayer().getRealCash() < 1000) {
                self.say("강림 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainRealCash(-1000, true);
        } else if (payType == ImprintedStonePayType.Promotion.getType()) {
            if (getPlayer().getHongboPoint() < 1000) {
                self.say("홍보 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainHongboPoint(-1000, true);
        } else if (payType == ImprintedStonePayType.Meso.getType()) {
            if (getPlayer().getMeso() < 500000000) {
                self.say("메소 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainMeso(-500000000, true);
        } else if (payType == ImprintedStonePayType.RoyalCash.getType()) {
            if (getPlayer().getCashPoint() < 1000) {
                self.say("#fs11#강림 캐시가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainCashPoint(-1000);
        } else if (payType == ImprintedStonePayType.RoyalDPoint.getType()) {
            if (getPlayer().getDonationPoint() < 3000) {
                self.say("#fs11#강림 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainDonationPoint(-3000);
        } else if (payType == ImprintedStonePayType.RoyalRedBall.getType()) {
            if (target.exchange(4031227, -200) < 0) {
                self.say("#fs11#찬란한 빛의 결정이 부족하여 세공을 진행할 수 없어요.");
                return;
            }
        }
        int plusValue = getPlayer().getOneInfoQuestInteger(133333, "plusValue" + index);
        int plusOption_ = getPlayer().getOneInfoQuestInteger(133333, "plusOption" + index);
        int remainCraftMinus = 10 - craftCount;
        int minusOption_ = getPlayer().getOneInfoQuestInteger(133333, "minusOption" + index);
        int minusValue = getPlayer().getOneInfoQuestInteger(133333, "minusValue" + index);

        ImprintedStoneOption minusOption = ImprintedStoneOption.getByOption(minusOption_);

        if (plusOption_ == 0) {
            if (DBConfig.isGanglim) {
                self.say("#fs11#플러스 세공을 1회 이상 해야 마이너스 세공이 가능해요.");    
            } else {
                self.say("플러스 세공을 1회 이상 해야 마이너스 세공이 가능해요.");
            }
            return;
        }
        if (minusOption_ == 0) {
            int[] options = new int[] {
                    ImprintedStoneOption.DamR.getOption(),
                    ImprintedStoneOption.AllStatP.getOption()
            };
            int minusOption2 = itemID == 2432127 ? options[Randomizer.rand(0, 1)] : ImprintedStoneOption.DamR.getOption();
            if (plusOption_ == ImprintedStoneOption.PMDR.getOption()) {
                // 플러스 옵션이 최종 데미지 증가면 마이너스 옵션은 확정으로 최종 데미지
                minusOption2 = ImprintedStoneOption.PMDR.getOption();
            }
            getPlayer().updateOneInfo(133333, "minusOption" + index, String.valueOf(minusOption2));
        }
        int[] rate = new int[] {
                30, 30, 30, 30, 50, 50, 50, 70, 70, 70
        };
        if (Randomizer.isSuccess(rate[craftCount])) {
            int delta = itemID == 2432127 ? 1 : 2;
            int value = getPlayer().getOneInfoQuestInteger(133333, "minusValue" + index);

            getPlayer().updateOneInfo(133333, "minusValue" + index, String.valueOf(value + delta));
        }
        getPlayer().updateOneInfo(133333, "craftMinus" + index, String.valueOf(getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index) + 1));


        minusOption_ = getPlayer().getOneInfoQuestInteger(133333, "minusOption" + index);
        minusValue = getPlayer().getOneInfoQuestInteger(133333, "minusValue" + index);
        minusOption = ImprintedStoneOption.getByOption(minusOption_);
        String v10 = "";
        if (DBConfig.isGanglim) {
            v10 += "#fs11#";
        }
        v10 += "#e[마이너스 세공]#n\r\n#b" + (getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index)) + "번째 세공이 완료되었어요.\r\n\r\n";
        v10 += "#e남은 마이너스 세공 횟수 : " + (remainCraftMinus - 1) + "\r\n";
        v10 += ("#b마이너스 옵션 : " + String.format(minusOption.getDesc(), "-", minusValue, "%", "") + "\r\n");
        if (payType == ImprintedStonePayType.Donation) {
            v10 += "#r현재 보유중인 강림 포인트 : " + NumberFormat.getInstance().format(getPlayer().getRealCash());
        } else if (payType == ImprintedStonePayType.Promotion) {
            v10 += "#r현재 보유중인 홍보 포인트 : " + NumberFormat.getInstance().format(getPlayer().getHongboPoint());
        } else if (payType == ImprintedStonePayType.Meso) {
            v10 += "#r현재 보유중인 메소 : " + NumberFormat.getInstance().format(getPlayer().getMeso());
        } else if (payType == ImprintedStonePayType.RoyalCash) {
            v10 += "#r현재 보유중인 강림 캐시 : " + NumberFormat.getInstance().format(getPlayer().getCashPoint());
        } else if (payType == ImprintedStonePayType.RoyalDPoint) {
            v10 += "#r현재 보유중인 강림 포인트 : " + NumberFormat.getInstance().format(getPlayer().getDonationPoint());
        } else if (payType == ImprintedStonePayType.RoyalRedBall) {
            v10 += "#r현재 보유중인 찬란한 빛의 결정 : " + getPlayer().getItemQuantity(4031227, false);
        }
        objects.utils.FileoutputUtil.log("./TextLog/EquipStoneCraft.txt", "각인석 세공(마이너스) (슬롯 : " + index + ", 아이템ID : " + itemID + ", 옵션 : " + minusOption.name() + ", 값 : " + minusValue + ", 지불 방식 : " + payType.name() + ", 사용자 : " + getPlayer().getName() + ")\r\n");

        v10 += "\r\n#b#n#L0#한 번 더 세공한다.#l\r\n";
        v10 += "#L1#세공을 그만둔다.#l";
        int v11 = self.askMenu(v10);
        //if (getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index) >= 10 &&
        //        getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index) >= 10) {
            getPlayer().checkImprintedStone();
        //}
        if (v11 == 0) {
            doMinusCraft(itemID, index, payType);
        }
    }

    public void doPlusCraft(int itemID, int index, ImprintedStonePayType payType) {
        String payMessage = "1,000 강림 포인트";
        if (payType == ImprintedStonePayType.Promotion) {
            payMessage = "1,000 홍보 포인트";
        } else if (payType == ImprintedStonePayType.Meso) {
            payMessage = "500,000,000 메소";
        } else if (payType == ImprintedStonePayType.RoyalCash) {
            payMessage = "1,000 강림 캐시";
        } else if (payType == ImprintedStonePayType.RoyalDPoint) {
            payMessage = "3,000 강림 포인트";
        } else if (payType == ImprintedStonePayType.RoyalRedBall) {
            payMessage = "찬란한 빛의 결정 200개";
        }
        if (DBConfig.isGanglim) {
            if (0 == self.askYesNo("#fs11#정말 #e" + payMessage + "#n를 사용하여 세공을 할 건가요?")) {
                return;
            }
        } else {
            if (0 == self.askYesNo("정말 #e" + payMessage + "#n를 사용하여 세공을 할 건가요?")) {
                return;
            }
        }
        
        int craftCount = getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index);
        if (craftCount >= 10) {
            if (DBConfig.isGanglim) {
                self.say("#fs11#이미 플러스 세공을 완료한 각인석인 거 같네요.");
            } else {
                self.say("이미 플러스 세공을 완료한 각인석인 거 같네요.");
            }
            return;
        }
        if (payType == ImprintedStonePayType.Donation.getType()) {
            if (getPlayer().getRealCash() < 1000) {
                self.say("강림 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainRealCash(-1000, true);
        } else if (payType == ImprintedStonePayType.Promotion.getType()) {
            if (getPlayer().getHongboPoint() < 1000) {
                self.say("홍보 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainHongboPoint(-1000, true);
        } else if (payType == ImprintedStonePayType.Meso.getType()) {
            if (getPlayer().getMeso() < 500000000) {
                self.say("메소 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainMeso(-500000000, true);
        } else if (payType == ImprintedStonePayType.RoyalCash.getType()) {
            if (getPlayer().getCashPoint() < 1000) {
                self.say("#fs11#강림 캐시가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainCashPoint(-1000);
        } else if (payType == ImprintedStonePayType.RoyalDPoint.getType()) {
            if (getPlayer().getDonationPoint() < 3000) {
                self.say("#fs11#강림 포인트가 부족하여 세공을 진행할 수 없어요.");
                return;
            }
            getPlayer().gainDonationPoint(-3000);
        } else if (payType == ImprintedStonePayType.RoyalRedBall.getType()) {
            if (target.exchange(4031227, -200) < 0) {
                self.say("#fs11#찬란한 빛의 결정이 부족하여 세공을 진행할 수 없어요.");
                return;
            }
        }
        int remainCraftPlus = 10 - craftCount;
        int plusOption_ = getPlayer().getOneInfoQuestInteger(133333, "plusOption" + index);
        int plusValue = getPlayer().getOneInfoQuestInteger(133333, "plusValue" + index);
        ImprintedStoneOption plusOption = ImprintedStoneOption.getByOption(plusOption_);

        if (plusOption_ == 0) {
            int[] options = new int[] {
                    ImprintedStoneOption.AllStatP.getOption(),
                    ImprintedStoneOption.BossDamageR.getOption(),
                    ImprintedStoneOption.IgnoreMobPdpR.getOption(),
                    ImprintedStoneOption.CriticalRate.getOption()
            };
            if (itemID == 2432126) {
                options = new int[] {
                        ImprintedStoneOption.AllStatP.getOption(),
                        ImprintedStoneOption.PadR.getOption(),
                        ImprintedStoneOption.MadR.getOption(),
                        ImprintedStoneOption.CriticalDamageR.getOption(),
                        ImprintedStoneOption.BossDamageR.getOption(),
                        ImprintedStoneOption.IgnoreMobPdpR.getOption(),
                        ImprintedStoneOption.CriticalRate.getOption(),
                        ImprintedStoneOption.PMDR.getOption()
                };
            }
            int plusOption2 = options[Randomizer.rand(0, options.length - 1)];
            if (Randomizer.isSuccess(2) && itemID == 2432126) {
                plusOption2 = ImprintedStoneOption.PMDR.getOption();
            }
            getPlayer().updateOneInfo(133333, "plusOption" + index, String.valueOf(plusOption2));
        }

        int[] rate = new int[] {
                70, 70, 70, 50, 50, 50, 50, 30, 30, 30
        };
        int delta = 2;
        int po = getPlayer().getOneInfoQuestInteger(133333, "plusOption" + index);
        if (itemID == 2432126) {
            if (po == ImprintedStoneOption.AllStatP.getOption() || po == ImprintedStoneOption.BossDamageR.getOption() ||
                    po == ImprintedStoneOption.IgnoreMobPdpR.getOption() || po == ImprintedStoneOption.CriticalRate.getOption()) {
                delta = 5;
            }
        }

        if (Randomizer.isSuccess(rate[craftCount])) {
            int value = getPlayer().getOneInfoQuestInteger(133333, "plusValue" + index);
            getPlayer().updateOneInfo(133333, "plusValue" + index, String.valueOf(value + delta));
        }
        getPlayer().updateOneInfo(133333, "craftPlus" + index, String.valueOf(getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index) + 1));

        plusOption_ = getPlayer().getOneInfoQuestInteger(133333, "plusOption" + index);
        plusValue = getPlayer().getOneInfoQuestInteger(133333, "plusValue" + index);
        plusOption = ImprintedStoneOption.getByOption(plusOption_);
        String v10 = "";
        if (DBConfig.isGanglim) {
            v10 += "#fs11#";
        }
        v10 += "#e[플러스 세공]#n\r\n#b" + (getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index)) + "번째 세공이 완료되었어요.\r\n\r\n";
        v10 += "#e남은 플러스 세공 횟수 : " + (remainCraftPlus - 1) + "\r\n";
        v10 += ("#b플러스 옵션 : " + String.format(plusOption.getDesc(), "+", plusValue, "%", "") + "\r\n");
        if (payType == ImprintedStonePayType.Donation) {
            v10 += "#r현재 보유중인 강림 포인트 : " + NumberFormat.getInstance().format(getPlayer().getRealCash());
        } else if (payType == ImprintedStonePayType.Promotion) {
            v10 += "#r현재 보유중인 홍보 포인트 : " + NumberFormat.getInstance().format(getPlayer().getHongboPoint());
        } else if (payType == ImprintedStonePayType.Meso) {
            v10 += "#r현재 보유중인 메소 : " + NumberFormat.getInstance().format(getPlayer().getMeso());
        } else if (payType == ImprintedStonePayType.RoyalCash) {
            v10 += "#r현재 보유중인 강림 캐시 : " + NumberFormat.getInstance().format(getPlayer().getCashPoint());
        } else if (payType == ImprintedStonePayType.RoyalDPoint) {
            v10 += "#r현재 보유중인 강림 포인트 : " + NumberFormat.getInstance().format(getPlayer().getDonationPoint());
        } else if (payType == ImprintedStonePayType.RoyalRedBall) {
            v10 += "#r현재 보유중인 찬란한 빛의 결정 : " + getPlayer().getItemQuantity(4031227, false);
        }
        v10 += "\r\n#b#n#L0#한 번 더 세공한다.#l\r\n";
        v10 += "#L1#세공을 그만둔다.#l";
        //if (getPlayer().getOneInfoQuestInteger(133333, "craftMinus" + index) >= 10 &&
        //    getPlayer().getOneInfoQuestInteger(133333, "craftPlus" + index) >= 10) {
            getPlayer().checkImprintedStone();
        //}
        objects.utils.FileoutputUtil.log("./TextLog/StoneCraft.txt", "각인석 세공(플러스) (슬롯 : " + index + ", 아이템ID : " + itemID + ", 옵션 : " + plusOption.name() + ", 값 : " + plusValue + ", 지불 방식 : " + payType.name() +", 사용자 : " + getPlayer().getName() + ")\r\n");

        int v11 = self.askMenu(v10);
        if (v11 == 0) {
            doPlusCraft(itemID, index, payType);
        }
    }


    public void JinPromotionReward() {
        if (DBConfig.isGanglim) {
            return;
        }
        initNPC(MapleLifeFactory.getNPC(9010032));

        var v0 = "안녕하세요, 운영자님.\r\n원활한 홍보 누적 보상 지급을 위해 준비된 서비스입니다.\r\n정해진 홍보 일수를 달성한 유저에게 아래 훈장을 수령하여 지급해 주세요.\r\n\r\n";
        v0 += "#e[홍보 누적 훈장 지급 기준]#n\r\n";
        v0 += "15일차 - #b#z1142070##k#r(올스탯 250, 공/마 200)#k\r\n";
        v0 += "100일차 - #b#z1142072##k#r(올스탯 400, 공/마 350)#k\r\n\r\n";
        v0 += "#b#L0#강림 서포터 훈장을 생성합니다.#l\r\n";
        v0 += "#L1#강림 서포터즈 훈장을 생성합니다.#l\r\n";

        int v1 = self.askMenu(v0);
        if (v1 == 0) {
            Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142070);
            if (equip != null) {
                equip.setStr((short) 250);
                equip.setDex((short) 250);
                equip.setInt((short) 250);
                equip.setLuk((short) 250);
                equip.setWatk((short) 200);
                equip.setMatk((short) 200);

                MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                self.say("생성이 완료되었습니다.");
            }
        } else if (v1 == 1) {
            Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142072);
            if (equip != null) {
                equip.setStr((short) 400);
                equip.setDex((short) 400);
                equip.setInt((short) 400);
                equip.setLuk((short) 400);
                equip.setWatk((short) 350);
                equip.setMatk((short) 350);

                MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                self.say("생성이 완료되었습니다.");
            }
        }
    }
    public void JinDonationReward() {
        if (DBConfig.isGanglim) {
            return;
        }
        initNPC(MapleLifeFactory.getNPC(9010033));

        int totalPrice = getTotalPrice();
        //totalPrice = 50000000; //TEST

	/*if (true) {
		self.say("점검중입니다.");
		return;
	}*/
        String v0 = "지급 받을 보상을 선택해주시기 바랍니다.\r\n\r\n#b";
        if (totalPrice < 25000000) {
            self.say("지급 받을 보상이 없습니다.");
        } else {
            boolean find = false;
            if (totalPrice >= 25000000) {
                int check = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_2500");
                if (0 == check) {
                    v0 += "#L2500#2500만원 보상을 수령받겠습니다.#l\r\n";
                    find = true;
                }
            }
            if (totalPrice >= 30000000) {
                int check = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_3000");
                if (0 == check) {
                    v0 += "#L3000#3000만원 보상을 수령받겠습니다.#l\r\n";
                    find = true;
                }

            }
            if (totalPrice >= 35000000) {
                int check = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_3500");
                if (0 == check) {
                    v0 += "#L3500#3500만원 보상을 수령받겠습니다.#l\r\n";
                    find = true;
                }
            }
            if (totalPrice >= 40000000) {
                int check = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_4000");
                if (0 == check) {
                    v0 += "#L4000#4000만원 보상을 수령받겠습니다.#l\r\n";
                    find = true;
                }
            }
            if (totalPrice >= 43000000) {
                int under4000 = totalPrice - 43000000;
                int count = under4000 / 3000000  + 1; // 4300때 기본 1회 가능
                int getCount = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_4300");
                int remain = count - getCount;
                if (remain > 0) {
                    v0 += "#L4300#4300만원 보상을 수령받겠습니다. (" + remain + "회 수령 가능)#l\r\n";
                    find = true;
                }
            }
            if (!find) {
                self.say("지급 받을 보상이 없습니다.");
                return;
            }
        }
        int v1 = self.askMenu(v0);
        switch (v1) {
            case 2500:
                if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
                    Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142107);
                    if (equip != null) {
                        equip.setStr((short) 6500);
                        equip.setDex((short) 6500);
                        equip.setInt((short) 6500);
                        equip.setLuk((short) 6500);
                        equip.setWatk((short) 5500);
                        equip.setMatk((short) 5500);

                        if (target.exchange(2049376, 3, 2432098, 8, 5121060, 50) > 0) {
                            MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                            getPlayer().setEnchantPoint(getPlayer().getEnchantPoint() + 250000);
                            getPlayer().updateOneInfo(1235999, "get_reward_2500", "1");
                            self.say("보상 지급이 완료되었습니다.");
                        }
                    }
                } else {
                    self.say("장비 인벤토리 공간을 확보하고 다시 시도해주세요.");
                }
                break;
            case 3000:
                if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
                    Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142108);
                    if (equip != null) {
                        equip.setStr((short) 6800);
                        equip.setDex((short) 6800);
                        equip.setInt((short) 6800);
                        equip.setLuk((short) 6800);
                        equip.setWatk((short) 5800);
                        equip.setMatk((short) 5800);

                        MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);

                        getPlayer().gainRealCash(1500000, true);
                        getPlayer().updateOneInfo(1235999, "get_reward_3000", "1");
                        self.say("보상 지급이 완료되었습니다.");
                    }
                } else {
                    self.say("장비 인벤토리 공간을 확보하고 다시 시도해주세요.");
                }
                break;
            case 3500:
                if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
                    Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142109);
                    if (equip != null) {
                        equip.setStr((short) 7200);
                        equip.setDex((short) 7200);
                        equip.setInt((short) 7200);
                        equip.setLuk((short) 7200);
                        equip.setWatk((short) 6200);
                        equip.setMatk((short) 6200);

                        if (target.exchange(2049376, 5, 2432098, 8, 2432097, 1, 5121060, 50) > 0) {
                            MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                            getPlayer().setEnchantPoint(getPlayer().getEnchantPoint() + 300000);
                            getPlayer().updateOneInfo(1235999, "get_reward_3500", "1");
                            self.say("보상 지급이 완료되었습니다.");
                        }
                    }
                } else {
                    self.say("장비 인벤토리 공간을 확보하고 다시 시도해주세요.");
                }
                break;
            case 4000:
                // SAVIOR
                if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
                    if (0 == getPlayer().getOneInfoQuestInteger(1235999, "get_reward_4000")) {
                        Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1142110);
                        if (equip != null) {
                            equip.setStr((short) 7500);
                            equip.setDex((short) 7500);
                            equip.setInt((short) 7500);
                            equip.setLuk((short) 7500);
                            equip.setWatk((short) 6500);
                            equip.setMatk((short) 6500);

                            getPlayer().gainRealCash(2000000, true);
                            MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                            getPlayer().updateOneInfo(1235999, "get_reward_4000", "1");
                            self.say("SAVIOR 보상 지급이 완료되었습니다.");
                        }
                    }
                } else {
                    self.say("장비 인벤토리 공간을 확보하고 다시 시도해주세요.");
                }
                break;
            case 4300:
                boolean find = false;
                // ENDLESS
                int under4000 = totalPrice - 43000000;
                int count = under4000 / 3000000 + 1;
                int getCount = getPlayer().getOneInfoQuestInteger(1235999, "get_reward_4300");
                int remain = count - getCount;
                if (remain > 0) {
                    if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 6) {
                        getPlayer().updateOneInfo(1235999, "get_reward_4300", String.valueOf(getCount + 1));

                        int[] endless = {1142242, 1142243, 1142244, 1142245, 1142246, 1142247};
                        for (int end : endless) {
                        	Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(end);
                            if (equip != null) {
                                equip.setStr((short) 12000);
                                equip.setDex((short) 12000);
                                equip.setInt((short) 12000);
                                equip.setLuk((short) 12000);
                                equip.setWatk((short) 10000);
                                equip.setMatk((short) 10000);
                                equip.setExpiration(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30));

                                MapleInventoryManipulator.addFromDrop(getPlayer().getClient(), equip, true);
                            }
                        }
                        find = true;
                        self.say("ENDLESS 보상 지급이 완료되었습니다.");
                    } else {
                        self.say("ENDLESS 보상 지급 중 인벤토리 공간이 부족합니다.");
                        return;
                    }
                }
                if (!find) {
                    self.say("4300만원 누적 보상 중 받을 수 있는 누적 보상이 없습니다.\r\n#e(" + count + "회 수령 가능 중 " + remain + "회 수령함)");
                }
                break;
        }
    }

    public int getTotalPrice() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT * FROM `donation_log` WHERE `account` = ?");
            ps.setString(1, getClient().getAccountName());
            rs = ps.executeQuery();

            int totalPoint = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                String p = rs.getString("price").replace(",", "");

                if (!name.contains("패키지") && !name.contains("어린이날") || name.contains("추석") || name.contains("가정의달") || name.contains("3주년") || name.contains("크리스마스") || name.contains("상시패키지") || name.contains("2023")) {
                    totalPoint += Integer.parseInt(p);
                }
            }
            return totalPoint;
        } catch (SQLException e) {
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException e) {
            }
        }
    }
    
    public void RestPoint() {
    	if (DBConfig.isGanglim) {
    		return;
    	}
    	initNPC(MapleLifeFactory.getNPC(9010033));
    	int restPoint = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinRestPointReward.getQuestID(), "RestPoint");
    	String text = "안녕하세요.\r\n"
    			+ "모험가님의 성장을 돕기 위해 준비된 출석 시스템입니다.\r\n"
    			+ "잠시 앉아서 쉬고 계시면 저희가 준비한 선물을 드릴게요!\r\n\r\n"
    			+ "선물은 휴식 시간이 540분 누적될 때마다 하나씩!\r\n"
    			+ "총 15개를 받으실 수 있으니까 참고해 주세요!\r\n\r\n"
    			+ "현재 누적 휴식 시간 : " + restPoint + "분\r\n\r\n";
    	
    	text += "#L0# 출석 시스템에 관해 자세히 알고 싶습니다.#l\r\n";
    	text += "#L1# 누적 휴식 시간 보상을 수령하고 싶습니다.#l\r\n";
    	text += "#L2# 누적 휴식 시간을 초기화하고 싶습니다.#l\r\n";
    	
    	int menu = self.askMenu(text);
    	
    	int[][] restTimeRewards = {
    			{540, 2632860, 1},
    			{1080, 5680157, 3},
    			{1620, 2049360, 10},
    			{2160, 2434557, 3},
    			{2700, 2439605, 5},
    			{3240, 2028273, 5},
    			{3780, 5068300, 3},
    			{4320, 5121060, 2},
    			{4860, 2049762, 1},
    			{5400, 2049360, 10},
    			{5940, 2434557, 5},
    			{6480, 2049371, 3},
    			{7020, 2434558, 3},
    			{7560, 2049376, 1},
    			{8100, 5060048, 10}
    			};
    	
    	if (menu == 0) {
    		text = "누적 휴식 시간에 따라 아래의 보상을 획득할 수 있으며,\r\n";
    		text += "휴식 시간은 마을에서 의자 또는 라이딩에 앉아 있는 동안만 누적됩니다.\r\n";
    		for (int[] timeReward : restTimeRewards) {
        		int time = timeReward[0];
        		int itemID = timeReward[1];
        		int itemQty = timeReward[2];
        		
        		text += time + "분 - #i" + itemID + "# #t" + itemID + "# " + itemQty + "개 \r\n";
        	}
    		self.sayOk(text);
    		return;
    	}
    	else if (menu == 1) {
    		text = "휴식시간 누적 보상은 아래와 같습니다. (현재 누적 : " + restPoint + "분)\r\n";
    		for (int i = 0; i < restTimeRewards.length; i++) {
        		int[] timeReward = restTimeRewards[i];
        		int time = timeReward[0];
        		int itemID = timeReward[1];
        		int itemQty = timeReward[2];
        		String timeKey = "recv" + time; 
        		int timeValue = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinRestPointReward.getQuestID(), timeKey);
        		if (restPoint < time) {
        			text += "#L" + i + "#" + time + "분 #i" + itemID + "# #t" + itemID + "# " + itemQty + "개 #r수령불가#k#l\r\n";
        		}
        		else {
        			text += "#L" + i + "#" + time + "분 #i" + itemID + "# #t" + itemID + "# " + itemQty + "개 " + (timeValue == 0 ? "#b수령가능#k" : "#r수령완료#k") + "#l\r\n";
        		}
        	}
    		int sel = self.askMenu(text);
    		if (sel < 0 || sel >= restTimeRewards.length) return;
    		int[] timeReward = restTimeRewards[sel];
    		int time = timeReward[0];
    		int itemID = timeReward[1];
    		int itemQty = timeReward[2];
    		String timeKey = "recv" + time; 
    		int timeValue = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinRestPointReward.getQuestID(), timeKey);
    		if (restPoint < time) {
    			self.sayOk("휴식 시간이 부족하여 해당보상을 수령하지 못했습니다.");
    			return;
    		}
    		if (timeValue == 0) {
    			if (getPlayer().getInventory(GameConstants.getInventoryType(itemID)).getNumFreeSlot() > 0) {
    				getPlayer().gainItem(itemID, itemQty);
    				getPlayer().updateOneInfo(QuestExConstants.JinRestPointReward.getQuestID(), timeKey, "1");
    				self.sayOk("보상이 지급되었습니다.");
    			}
    			else {
    				self.sayOk("인벤토리 공간이 부족하여 보상을 수령하지 못했습니다.");
    			}
    		}
    		else {
    			self.sayOk("해당 보상은 이미 수령하였습니다.");
    		}
    		return;
    	}
    	else if (menu == 2) {
    		text = "누적 휴식 시간을 초기화하기 위해서는 \r\n#b60,000 강림 포인트#k가 필요합니다.\r\n";
    		text += "초기화 시 수령하지 않은 보상은 수령할 수 없으니,\r\n";
    		text += "반드시 수령하지 않은 보상이 있는지 확인해 주세요.\r\n\r\n";
    		text += "#fc0xFFB2B2B2#현재 누적 휴식 시간 : " + restPoint + "분#k\r\n";
    		text += "#L0#누적 휴식 시간을 초기화합니다.#l";
    		if (0 == self.askMenu(text)) {
    			if (getPlayer().getRealCash() < 60000) {
                    self.say("강림 포인트가 부족하여 초기화할 수 없습니다.");
                    return;
                }
    			getPlayer().gainRealCash(-60000);
    			getPlayer().updateOneInfo(QuestExConstants.JinRestPointReward.getQuestID(), "RestPoint", "0");
    			for (int i = 0; i < restTimeRewards.length; i++) {
            		int[] timeReward = restTimeRewards[i];
            		int time = timeReward[0];
            		String timeKey = "recv" + time; 
            		getPlayer().updateOneInfo(QuestExConstants.JinRestPointReward.getQuestID(), timeKey, "0");
            	}
    			self.sayOk("초기화가 완료되었습니다.");
    		}
    		return;
    	}
    }
    
    public void DailyMission() {
    	if (DBConfig.isGanglim) {
    		return;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	String today = sdf.format(System.currentTimeMillis());
    	int missionTime = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyMissionTime");
    	if (missionTime == 0 || Integer.parseInt(today) != missionTime) { //첫시작 또는 날짜바뀜
    		getPlayer().updateOneInfo(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyMissionTime", today);
    		getPlayer().updateOneInfo(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyMissionClear", "0");
    		getPlayer().updateOneInfo(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyLevelMob", "0");
    		getPlayer().updateOneInfo(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyRuneUse", "0");
    	}
    	int dailyLevelMob = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyLevelMob");
		int dailyRuneUse = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyRuneUse");
		int dailyMissionClear = getPlayer().getOneInfoQuestInteger(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyMissionClear");
		boolean checkLevelMob = dailyLevelMob >= 15000;
		boolean checkRuneUse = dailyRuneUse >= 5;
		if (dailyMissionClear > 0) {
			self.sayOk("금일 일일 미션을 이미 완료하셨습니다.");
			return;
		}
		String text = "아래 미션을 전부 완료하면 네오 스톤 200개를 수령할 수 있습니다.\r\n\r\n";
		text += "- 레벨 범위 몬스터 15,000 마리 처치 " + (checkLevelMob ? "#b완료#k" : ("#b진행중("  + dailyLevelMob + "/15000)#k")) + "\r\n";
		text += "- 룬 5회 사용 " + (checkRuneUse ? "#b완료#k" : ("#b진행중("  + dailyRuneUse + "/5)#k")) + "\r\n\r\n";
		text += "#L0#일일미션 보상을 수령합니다.#l";
		if (0 == self.askMenu(text)) {
			if (checkLevelMob && checkRuneUse) {
                getPlayer().gainStackEventGauge(0, 200, true);
                getPlayer().updateOneInfo(QuestExConstants.JinQuestExAccount.getQuestID(), "DailyMissionClear", "1");
                self.sayOk("일일 임무를 완료하여 네오 스톤 200개를 획득하였습니다.");
			}
			else {
				self.sayOk("아직 일일 미션을 모두 클리어하지 않았습니다.");
				return;
			}
		}
    }
    
    public void sendCubeLevelUpInfo() {
    	GradeRandomOption[] options = {GradeRandomOption.Red, GradeRandomOption.Black, GradeRandomOption.Additional, GradeRandomOption.AmazingAdditional};
    	String cubeTotalInfo = "큐브 등급 상승까지 #b남은 큐브 사용 횟수#k를 확인해 보세요.\r\n"
    			+ "해당 수치는 등급 상승 시 초기화되며, #b계정 내 공유#k됩니다.\r\n\r\n";
    	for (GradeRandomOption option : options) {
    		String cubeString = "";
			switch (option) {
				case Red:
					cubeString = "레드 큐브";
					break;
				case Black:
					cubeString = "블랙 큐브";
					break;
				case Additional:
					cubeString = "에디셔널 큐브";
					break;
				case AmazingAdditional:
					cubeString = "화이트 에디셔널 큐브";
					break;
			}
			cubeTotalInfo += "#e[" + cubeString + "]#n\r\n";
			String[] levelUps = {"RtoE", "EtoU", "UtoL"};
			for (String levelUp : levelUps) {
				int tryCount = getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), option.toString() + levelUp);
				int grade = 1;
				String gradeString = "";
				if (levelUp.equals("RtoE")) {
					grade = 1;
					gradeString = "레어에서 에픽";
				}
				else if (levelUp.equals("EtoU")) {
					grade = 2;
					gradeString = "에픽에서 유니크";
				}
				else if (levelUp.equals("UtoL")) {
					grade = 3;
					gradeString = "유니크에서 레전더리";
				}
				int levelUpCount = GameConstants.getCubeLevelUpCount(option, grade);
				cubeTotalInfo += gradeString + " " + levelUpCount + "회 중 " + tryCount + "회 누적\r\n"; 
			}
			if (option != GradeRandomOption.AmazingAdditional) {
				cubeTotalInfo += "\r\n";
			}
    	}
    	self.sayOk(cubeTotalInfo);
    }
}