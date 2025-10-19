package script.item;

import database.DBConfig;
import network.discordbot.DiscordBotHandler;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.MapleInventoryType;
import objects.utils.Randomizer;
import scripting.newscripting.ScriptEngineNPC;

import java.util.*;

public class SymbolVoucher extends ScriptEngineNPC {

    public void consume_2437750() {
        //1개
        changeArcaneSymbolVoucher(1);
    }

    public void consume_2437760() {
        //5개
        changeArcaneSymbolVoucher(5);
    }

    public void consume_2437880() {
        //2개
        changeArcaneSymbolVoucher(2);
    }

    public void consume_2438141() {
        //1개
        changeArcaneSymbolVoucher(1);
    }

    public void consume_2439301() {
        //20개
        changeArcaneSymbolVoucher(20);
    }

    public void consume_2439305() {
        //1개
        changeArcaneSymbolVoucher(1);
    }

    public void consume_2631878() {
        //100개
        changeArcaneSymbolVoucher(100);
    }

    public void consume_2631906() {
        //10개
        changeArcaneSymbolVoucher(10);
    }

    public void consume_2633385() {
        //100개
        changeArcaneSymbolVoucher(100);
    }

    public void consume_2630981() {
        //5개
        changeArcaneSymbolVoucher(5);
    }

    public void consume_2630983() {
        //랜덤
        /**
         * 1개 (45%)
         * 2개 (45%)
         * 5개 (9%)
         * 10개 (0.8%)
         * 50개 (0.16%)
         * 100개 (0.04%)
         * */
        HashMap<Integer, Double> itemQty = new HashMap();
        itemQty.put(1, 45.0d);
        itemQty.put(2, 45.0d);
        itemQty.put(5, 9.0d);
        itemQty.put(10, 0.8d);
        itemQty.put(50, 0.16d);
        itemQty.put(100, 0.04d);
        List<Integer> keys = new ArrayList<>(itemQty.keySet());
        Collections.shuffle(keys);

        double percent = 45.0d + 45.0d + 9.0d + 0.8d + 0.16d + 0.04d;
        double random = percent - (Randomizer.nextDouble() * 100);
        double stack = 0.0d;
        int quantity = 0;
        Iterator<Integer> ite = keys.iterator();
        while (ite.hasNext() && quantity == 0) {
            int a = ite.next();
            Double p = itemQty.get(a);
            stack += p;
            if (stack >= random) {
                quantity = a;
            }
        }
        changeArcaneSymbolVoucher(quantity);
    }

    public void changeArcaneSymbolVoucher(int quantity) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.sayOk("소비창에 여유 공간이 부족합니다. 확인 후 다시 사용해주세요.");
            return;
        }
        if (target.exchange(itemID, -1, 2630437, quantity) > 0) {
            self.sayOk("선택 아케인심볼 " + quantity + "개 교환이 완료되었습니다. 소비창을 확인해보세요.");
        } else {
            self.sayOk("소비창에 여유 공간이 부족합니다. 확인 후 다시 사용해주세요.");
        }
    }

    public void consume_2630437() {
        //교불 선택 아케인심볼 교환권
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 200) {
            self.sayOk("#fs11#200레벨 이상만 사용할 수 있습니다.");
            return;
        }
        String changeSymbol = "#fs11#";
        if (getPlayer().getLevel() >= 200) { //여로
            changeSymbol += "#L5# #b#i1712001:# #t1712001:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 210) { //츄츄
            changeSymbol += "#L4# #b#i1712002:# #t1712002:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 220) { //레헬른
            changeSymbol += "#L3# #b#i1712003:# #t1712003:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 225) { //아르카나
            changeSymbol += "#L2# #b#i1712004:# #t1712004:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 230) { //모라스
            changeSymbol += "#L1# #b#i1712005:# #t1712005:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 235) { //에스페라
            changeSymbol += "#L0# #b#i1712006:# #t1712006:# #k#l";
        }

        int v = self.askMenu("#fs11#받고 싶은 #b아케인심볼#k을 선택해 주세요!\r\n\r\n" + changeSymbol);
        int itemQty = getPlayer().getItemQuantity(itemID, false);
        String letter = "[아케인심볼 교환권 치트엔진 case변경 시도]\r\n 유저 닉네임 : " + getPlayer().getName() + "\r\n 레벨 : " + getPlayer().getLevel() + "\r\n 획득 시도한 심볼 : ";
        int number = self.askNumber("\r\n인벤토리에 #b#i" + itemID + ":# #t" + itemID + ":##k 아이템이 #b" + itemQty + "개#k 있습니다. #b#e한 번에 몇 개#n#k를 사용하시겠습니까?\r\n ", itemQty, 1, itemQty);
        int symbolID = 1712001;
        switch (v) {
            case 0: {
                symbolID = 1712006;
                if (getPlayer().getLevel() < 235) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "에스페라";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 1: {
                symbolID = 1712005;
                if (getPlayer().getLevel() < 230) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "모라스";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 2: {
                symbolID = 1712004;
                if (getPlayer().getLevel() < 225) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "아르카나";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 3: {
                symbolID = 1712003;
                if (getPlayer().getLevel() < 220) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "레헬른";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 4: {
                symbolID = 1712002;
                if (getPlayer().getLevel() < 210) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "츄츄아일랜드";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 5: {
                symbolID = 1712001;
                if (getPlayer().getLevel() < 200) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "소멸의여로";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
        }
        if (number < 0) {
            return;
        }
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < number) {
            self.say("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
        } else {
            if (1 == self.askYesNo("#fs11##b#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#k를 받으시겠습니까?")) {
                if (target.exchange(itemID, -number, symbolID, number) <= 0) {
                    self.sayOk("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
                } else {
                    self.sayOk("#fs11#\r\n#b#t" + symbolID + ":# " + number + "개#k를 지급해드렸습니다!\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0##b#e\r\n#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#n#k");
                }
            }
        }
    }

    public void consume_2630512() {
        //월드 내 나의 캐릭터 간 이동 가능 선택 아케인심볼 교환권
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 200) {
            self.sayOk("200레벨 이상만 사용할 수 있습니다.");
            return;
        }
        String changeSymbol = "#fs11#";
        if (getPlayer().getLevel() >= 200) { //여로
            changeSymbol += "#L5# #b#i1712001:# #t1712001:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 210) { //츄츄
            changeSymbol += "#L4# #b#i1712002:# #t1712002:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 220) { //레헬른
            changeSymbol += "#L3# #b#i1712003:# #t1712003:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 225) { //아르카나
            changeSymbol += "#L2# #b#i1712004:# #t1712004:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 230) { //모라스
            changeSymbol += "#L1# #b#i1712005:# #t1712005:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 235) { //에스페라
            changeSymbol += "#L0# #b#i1712006:# #t1712006:# #k#l";
        }

        int v = self.askMenu("#fs11#받고 싶은 #b아케인심볼#k을 선택해 주세요!\r\n\r\n" + changeSymbol);
        int itemQty = getPlayer().getItemQuantity(itemID, false);
        String letter = "[아케인심볼 교환권 치트엔진 case변경 시도]\r\n 유저 닉네임 : " + getPlayer().getName() + "\r\n 레벨 : " + getPlayer().getLevel() + "\r\n 획득 시도한 심볼 : ";
        int number = self.askNumber("\r\n인벤토리에 #b#i" + itemID + ":# #t" + itemID + ":##k 아이템이 #b" + itemQty + "개#k 있습니다. #b#e한 번에 몇 개#n#k를 사용하시겠습니까?\r\n ", itemQty, 1, itemQty);
        int symbolID = 1712001;
        switch (v) {
            case 0: {
                symbolID = 1712006;
                if (getPlayer().getLevel() < 235) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "에스페라";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 1: {
                symbolID = 1712005;
                if (getPlayer().getLevel() < 230) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "모라스";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 2: {
                symbolID = 1712004;
                if (getPlayer().getLevel() < 225) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "아르카나";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 3: {
                symbolID = 1712003;
                if (getPlayer().getLevel() < 220) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "레헬른";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 4: {
                symbolID = 1712002;
                if (getPlayer().getLevel() < 210) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "츄츄아일랜드";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 5: {
                symbolID = 1712001;
                if (getPlayer().getLevel() < 200) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "소멸의여로";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
        }
        if (number < 0) {
            return;
        }
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < number) {
            self.say("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
        } else {
            if (1 == self.askYesNo("#fs11##b#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#k를 받으시겠습니까?")) {
                if (target.exchange(itemID, -number, symbolID, number) <= 0) {
                    self.sayOk("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
                } else {
                    self.sayOk("#fs11#\r\n#b#t" + symbolID + ":# " + number + "개#k를 지급해드렸습니다!\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0##b#e\r\n#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#n#k");
                }
            }
        }
    }

    public void consume_2633336() {
        changeAuthenticSymbolVoucher(DBConfig.isGanglim ? 5 : 1);
    }

    public void consume_2633616() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 260) {
            self.sayOk("#fs11#260레벨 이상만 사용할 수 있습니다.");
            return;
        }
        String changeSymbol = "#fs11#";
        if (getPlayer().getLevel() >= 260) { //세르니움
            changeSymbol += "#L2# #b#i1713000:# #t1713000:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 270) { //아르크스
            changeSymbol += "#L1# #b#i1713001:# #t1713001:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 275) { // 오디움
            changeSymbol += "#L3# #b#i1713002:# #t1713002:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 280) { // 도원경
            changeSymbol += "#L4# #b#i1713003:# #t1713003:# #k#l\r\n";
        }
        if (getPlayer().getLevel() >= 280) { // 아르테리아
            changeSymbol += "#L5# #b#i1713004:# #t1713004:# #k#l";
        }
        int v = self.askMenu("#fs11#받고 싶은 #b어센틱심볼#k을 선택해 주세요!\r\n\r\n" + changeSymbol);
        int itemQty = getPlayer().getItemQuantity(itemID, false);
        String letter = "[어센틱심볼 교환권 치트엔진 case변경 시도]\r\n 유저 닉네임 : " + getPlayer().getName() + "\r\n 레벨 : " + getPlayer().getLevel() + "\r\n 획득 시도한 심볼 : ";
        int number = self.askNumber("\r\n인벤토리에 #b#i" + itemID + ":# #t" + itemID + ":##k 아이템이 #b" + itemQty + "개#k 있습니다. #b#e한 번에 몇 개#n#k를 사용하시겠습니까?\r\n ", itemQty, 1, itemQty);
        int symbolID = 1713000;
        switch (v) {
            case 1: {
                symbolID = 1713001;
                if (getPlayer().getLevel() < 270) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "아르크스";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 2: {
                symbolID = 1713000;
                if (getPlayer().getLevel() < 260) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "세르니움";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 3: {
                symbolID = 1713002;
                if (getPlayer().getLevel() < 275) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "오디움";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 4: {
                symbolID = 1713003;
                if (getPlayer().getLevel() < 280) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "도원경";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
            case 5: {
                symbolID = 1713004;
                if (getPlayer().getLevel() < 280) {
                    self.sayOk("#fs11#레벨이 부족하여 교환하실 수 없습니다.");
                    letter += "아르테리아";
                    //DiscordBotHandler.requestSendTelegram(letter, -460314003);
                    return;
                }
                break;
            }
        }
        if (number < 0) {
            return;
        }
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < number) {
            self.say("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
        } else {
            if (1 == self.askYesNo("#fs11##b#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#k를 받으시겠습니까?")) {
                if (target.exchange(itemID, -number, symbolID, number) <= 0) {
                    self.sayOk("#fs11#장비창을 " + number + "칸 이상 비우고 다시 사용해 주세요.");
                } else {
                    self.sayOk("#fs11#\r\n#b#t" + symbolID + ":# " + number + "개#k를 지급해드렸습니다!\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0##b#e\r\n#i" + symbolID + ":# #t" + symbolID + ":# " + number + "개#n#k");
                }
            }
        }
    }

    public void changeAuthenticSymbolVoucher(int quantity) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.sayOk("소비창에 여유 공간이 부족합니다. 확인 후 다시 사용해주세요.");
            return;
        }
        if (target.exchange(itemID, -1, 2633616, quantity) > 0) {
            self.sayOk("선택 어센틱심볼 " + quantity + "개 교환이 완료되었습니다. 소비창을 확인해보세요.");
        } else {
            self.sayOk("소비창에 여유 공간이 부족합니다. 확인 후 다시 사용해주세요.");
        }
    }
}
