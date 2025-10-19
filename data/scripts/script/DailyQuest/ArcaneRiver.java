package script.DailyQuest;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArcaneRiver extends ScriptEngineNPC {

    //슈피겔만 아케인리버 퀵패스
    @Script
    public void npc_3003146() {
        if (getPlayer().isQuestStarted(501551)) { //<소멸의 여로>로 스텝업!
            if (getPlayer().getOneInfoQuestInteger(501551, "value") < 1) {
                getPlayer().updateOneInfo(501551, "value", "1");
            }
            if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                getPlayer().updateOneInfo(501524, "state", "2");
            }
        }
        //getPlayer().send(HexTool.getByteArrayFromHexString("64 00 0D 8B 98 00 00 00 00"));
        //getPlayer().send(HexTool.getByteArrayFromHexString("64 00 0D 8C 98 00 00 00 00"));
        int level = getPlayer().getLevel();
        StringBuilder sb = new StringBuilder();
        int t0 = 5; //여로
        for (int i = 34164; i <= 34167; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                t0--;
            }
        }
        sb.append("t0=").append(t0).append(";");
        int t1 = 0; //츄츄
        if (level >= 210) {
            t1 = 3;
        }
        for (int i = 34227; i <= 34228; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                t1--;
            }
        }
        sb.append("t1=").append(t1).append(";");
        int t2 = 0; //레헬
        if (level >= 220) {
            t2 = 3;
        }
        for (int i = 34333; i <= 34334; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                t2--;
            }
        }
        sb.append("t2=").append(t2).append(";");
        int t3 = 0; //아르카나
        if (level >= 225) {
            t3 = 3;
        }
        for (int i = 34491; i <= 34492; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                t3--;
            }
        }
        sb.append("t3=").append(t3).append(";");
        int t4 = 0; //모라스
        if (level >= 230) {
            t4 = 3;
        }
        if (getPlayer().getLevel() >= 245) {
            t4--;
        }
        sb.append("t4=").append(t4).append(";");
        int t5 = 0; //에스페라
        if (level >= 235) {
            t5 = 3;
        }
        if (getPlayer().getLevel() >= 245) {
            t5--;
        }
        if (getPlayer().getLevel() >= 250) {
            t5--;
        }
        sb.append("t5=").append(t5).append(";");
        int c0 = 0; //여로
        for (int i = 34130; i <= 34150; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        for (int i = 39055; i <= 39063; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        if (getPlayer().getQuestStatus(34129) == 2) { //소멸의 여로 메인퀘가 깨져있으면
            c0 = t0;
        }
        sb.append("c0=").append(c0).append(";");
        int c1 = 0; //츄츄
        for (int i = 39017; i <= 39033; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c1++;
            }
        }
        for (int i = 39064; i <= 39070; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c1++;
            }
        }
        if (getPlayer().getQuestStatus(39014) == 2) { //츄츄 메인퀘가 깨져있으면
            c1 = t1;
        }
        sb.append("c1=").append(c1).append(";");
        int c2 = 0; //레헬
        for (int i = 34381; i <= 34394; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c2++;
            }
        }
        if (getPlayer().getQuestStatus(34378) == 2) { //레헬른 메인퀘가 깨져있으면
            c2 = t2;
        }
        sb.append("c2=").append(c2).append(";");
        int c3 = 0; //아르카나
        for (int i = 39038; i <= 39050; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c3++;
            }
        }
        if (getPlayer().getQuestStatus(39035) == 2) { //아르카나 메인퀘가 깨져있으면
            c3 = t3;
        }
        sb.append("c3=").append(c3).append(";");
        int c4 = 0; //모라스
        for (int i = 34276; i <= 34296; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c4++;
            }
        }
        if (getPlayer().getQuestStatus(34275) == 2) { //모라스 메인퀘가 깨져있으면
            c4 = t4;
        }
        sb.append("c4=").append(c4).append(";");
        int c5 = 0; //에스페라
        for (int i = 34780; i <= 34799; i++) {
            if (getPlayer().getQuestStatus(i) == 2) {
                c5++;
            }
        }
        if (getPlayer().getQuestStatus(34773) == 2) { //에스페라 메인퀘가 깨져있으면
            c5 = t5;
        }
        sb.append("c5=").append(c5).append(";");
        //흑흑 퀘스트만들때 이거 고려안하고 만들었어 ㅠ

        getPlayer().updateInfoQuest(39051, sb.toString());
        t0 = 3;
        if (getPlayer().getLevel() >= 210)
            t0--;
        if (getPlayer().getLevel() >= 220)
            t0--;
        t1 = 3; //배고픈무토
        if (getPlayer().getLevel() >= 220)
            t1--;
        if (getPlayer().getLevel() >= 225)
            t1--;
        t2 = 3; //드림브레이커
        if (getPlayer().getLevel() >= 225)
            t2--;
        if (getPlayer().getLevel() >= 230)
            t2--;
        t3 = 3;
        if (getPlayer().getLevel() >= 230)
            t3--;
        if (getPlayer().getLevel() >= 235)
            t3--;
        c0 = getPlayer().getOneInfoQuestInteger(34170, "count");
        c1 = getPlayer().GetCount("hungry_muto");
        c2 = getPlayer().GetCount("dream_breaker");
        c3 = getPlayer().getOneInfoQuestInteger(16214, "count");
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTimeSpectrum = null;
        Date lastTimeSavior = null;
        Date now = null;
        try {
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
        }
        try {
            lastTimeSpectrum = sdf.parse(getPlayer().getOneInfo(34170, "date"));
        } catch (Exception e) {
        }
        try {
            lastTimeSavior = sdf.parse(getPlayer().getOneInfo(16214, "date"));
        } catch (Exception e) {
        }
        if (lastTimeSpectrum != null) {
            if (!lastTimeSpectrum.equals(now)) {
                c0 = 0;
            }
        }
        if (lastTimeSavior != null) {
            if (!lastTimeSavior.equals(now)) {
                c3 = 0;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("t0=").append(t0).append(";").append("t1=").append(t1).append(";").append("t2=").append(t2).append(";").append("t3=").append(t3).append(";")
                .append("c0=").append(c0).append(";").append("c1=").append(c1).append(";").append("c2=").append(c2).append(";").append("c3=").append(c3).append(";");
        getPlayer().updateInfoQuest(39052, sb2.toString());
        getPlayer().send(arcaneRiverQuickPath(level >= 200 ? true : false));
    }

    public void arcaneRiverQuickPath0() {
        int t0 = 5;
        for (int i = 34164; i <= 34167; i++) { //여기수정
            if (getPlayer().getQuestStatus(i) == 2) {
                t0--;
            }
        }
        int c0 = 0;
        List<Integer> quests = new ArrayList<>();
        for (int i = 34130; i <= 34150; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        for (int i = 39055; i <= 39063; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 1 * getClient().getChannelServer().getVanishingJourneySymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getVanishingJourneySymbolBonusRate()) - basicPay; //여기수정
        String cityName = "소멸의 여로"; //여기수정
        String key = "c0"; //여기수정
        int itemId = 1712001; //여기수정
        int questId = 34129;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    public void arcaneRiverQuickPath1() {
        int t0 = 3;
        for (int i = 34227; i <= 34228; i++) { //여기수정
            if (getPlayer().getQuestStatus(i) == 2) {
                t0--;
            }
        }
        int c0 = 0;
        List<Integer> quests = new ArrayList<>();
        for (int i = 39017; i <= 39033; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        for (int i = 39064; i <= 39070; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 2 * getClient().getChannelServer().getChewChewSymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getChewChewSymbolBonusRate()) - basicPay; //여기수정
        String cityName = "츄츄 아일랜드"; //여기수정
        String key = "c1"; //여기수정
        int itemId = 1712002; //여기수정
        int questId = 39014;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    public void arcaneRiverQuickPath2() {
        int t0 = 3;
        for (int i = 34333; i <= 34334; i++) { //여기수정
            if (getPlayer().getQuestStatus(i) == 2) {
                t0--;
            }
        }
        int c0 = 0;
        List<Integer> quests = new ArrayList<>();
        for (int i = 34381; i <= 34394; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 2 * getClient().getChannelServer().getLachelnSymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getLachelnSymbolBonusRate()) - basicPay; //여기수정
        String cityName = "레헬른"; //여기수정
        String key = "c2"; //여기수정
        int itemId = 1712003; //여기수정
        int questId = 34378;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    public void arcaneRiverQuickPath3() {
        int t0 = 3;
        for (int i = 34491; i <= 34492; i++) { //여기수정
            if (getPlayer().getQuestStatus(i) == 2) {
                t0--;
            }
        }
        int c0 = 0;
        List<Integer> quests = new ArrayList<>();
        for (int i = 39038; i <= 39050; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 2 * getClient().getChannelServer().getArcanaSymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getArcanaSymbolBonusRate()) - basicPay; //여기수정
        String cityName = "아르카나"; //여기수정
        String key = "c3"; //여기수정
        int itemId = 1712004; //여기수정
        int questId = 39035;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    public void arcaneRiverQuickPath4() {
        int t0 = 3;
        if (getPlayer().getLevel() >= 245) {
            t0--;
        }
        int c0 = 0;
        List<Integer> quests = new ArrayList<>();
        for (int i = 34276; i <= 34296; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 2 * getClient().getChannelServer().getMorassSymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getMorassSymbolBonusRate()) - basicPay; //여기수정
        String cityName = "모라스"; //여기수정
        String key = "c4"; //여기수정
        int itemId = 1712005;
        int questId = 34275;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    public void arcaneRiverQuickPath5() {
        int t0 = 3; //에스페라
        if (getPlayer().getLevel() >= 245) {
            t0--;
        }
        if (getPlayer().getLevel() >= 250) {
            t0--;
        }
        int c0 = 0; //에스페라
        List<Integer> quests = new ArrayList<>();
        for (int i = 34780; i <= 34799; i++) { //여기수정
            quests.add(i);
            if (getPlayer().getQuestStatus(i) == 2) {
                c0++;
            }
        }
        int basicPay = 2 * getClient().getChannelServer().getArcanaSymbolBonusRate(); //여기수정
        int totalPay = (8 * getClient().getChannelServer().getArcanaSymbolBonusRate()) - basicPay; //여기수정
        String cityName = "에스페라";
        String key = "c5"; //여기수정
        int itemId = 1712006;
        int questId = 34773;
        totalPay -= basicPay * Integer.parseInt(getPlayer().getOneInfo(39051, key));
        arcaneRiverQuickPath(cityName, quests, key, t0, c0, basicPay, totalPay, itemId, questId);
    }

    @SuppressWarnings("deprecation")
    public void arcaneRiverQuickPath(String cityName, List<Integer> quests, String key, int t, int c, int basicPay, int totalPay, int itemId, int questID) {
        int commission = 500 * (t - c);
        if (1 == self.askYesNo(cityName + " 일일 퀘스트를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b" + commission + " 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i" + itemId + ":# #t" + itemId + ":# " + basicPay + "+" + totalPay + "개#k는 자네에게 주겠네.", ScriptMessageFlag.Scenario)) {
            if (getPlayer().getMaplePoints() >= commission) {
                if (target.exchange(itemId, (basicPay + totalPay)) > 0) {
                    MapleQuestStatus qStatus = new MapleQuestStatus(MapleQuest.getInstance(questID), 2);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    date.setDate(date.getDate() + 1);
                    String saveDate = sdf.format(date);
                    qStatus.setCustomData("0-0-0-0-0-n-n-n-n-n-" + saveDate);
                    getPlayer().updateQuest(qStatus);
                    for (Integer quest : quests) {
                        if (getPlayer().getQuestStatus(quest) == 1) {
                            getPlayer().forceCompleteQuest(quest);
                        }
                    }
                    getPlayer().updateOneInfo(39051, key, String.valueOf(t));
                    getPlayer().setMaplePoint(getPlayer().getMaplePoints() - commission);
                    self.say("완료되었네\r\n크크.. 또 이용해 주시게나.", ScriptMessageFlag.Scenario);
                } else {
                    self.say("인벤토리가 부족하군\r\n인벤토리를 비운 뒤 다시 이용해주게나");
                }
            } else {
                self.say("자네의 메이플 포인트가 부족한 것 같군 다시 확인해보시게나");
            }
        }
    }

    public void arcaneRiverQuickPath10() { //에르다스펙트럼

        int t = 3;
        if (getPlayer().getLevel() >= 210)
            t--;
        if (getPlayer().getLevel() >= 220)
            t--;
        int c = getPlayer().getOneInfoQuestInteger(34170, "count");

        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(34170, "date"));
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
        }
        if (lastTime != null) {
            if (!lastTime.equals(now)) {
                c = 0;
            }
        }
        int basicPay = 10;

        if (c > t) {
            c = t;
        }
        int commission = 500 * (t - c);
        int totalItem = (basicPay * 3) - (c * basicPay);
        int itemId = 1712001;
        if (1 == self.askAccept("#r에르다 스펙트럼#k을 처리해주면 되겠나?\r\n\r\n의뢰금은 #b" + commission + " 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i1712001:# #t1712001:# " + totalItem + "개#k는 자네에게 주겠네.", ScriptMessageFlag.Scenario)) {
            if (getPlayer().getMaplePoints() >= commission) {
                if (target.exchange(itemId, totalItem) > 0) {
                    getPlayer().updateOneInfo(34170, "count", "3");
                    getPlayer().updateOneInfo(34170, "date", sdf.format(new Date()));
                    getPlayer().updateOneInfo(39052, "c0", String.valueOf(t));
                    getPlayer().setMaplePoint(getPlayer().getMaplePoints() - commission);
                    self.say("완료되었네\r\n크크.. 또 이용해 주시게나.", ScriptMessageFlag.Scenario);
                } else {
                    self.say("인벤토리가 부족하군\r\n인벤토리를 비운 뒤 다시 이용해주게나");
                }
            } else {
                self.say("자네의 메이플 포인트가 부족한 것 같군 다시 확인해보시게나");
            }
        }
    }

    public void arcaneRiverQuickPath11() { //배고픈무토
        if (getPlayer().getMutoHighRank() == -1) {
            self.sayOk("#r배고픈 무토#k에서 자네의 최고 성과를 찾을 수 없는 것 같네.\r\n그걸 기준으로 일을 처리해야 공평하지 않겠나? 크크...\r\n\r\n(#b아케인리버 퀵패스#k 이용 전 최초 1회의 해당 컨텐츠 플레이가 필요합니다.)", ScriptMessageFlag.Scenario);
            return;
        }

        //#r배고픈 무토#k를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b500 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i1712002:# #t1712002:# 15개#k는 자네에게 주겠네.
        int t = 3;
        for (int i = 34227; i <= 34228; i++) { //여기수정
            if (getPlayer().getQuestStatus(i) == 2) {
                t--;
            }
        }
        int c = getPlayer().GetCount("hungry_muto");

        int basicPay = 5;
        int highDiff = getPlayer().getMutoHighDifficultly();
        int highRank = getPlayer().getMutoHighRank();
        if (highDiff == 2) { //어려움
            if (highRank == 1) { //A랭크
                basicPay = 4;
            } else if (highRank == 2) { //B랭크
                basicPay = 3;
            }
        } else if (highDiff == 1) {
            if (highRank == 0) {
                basicPay = 3;
            } else if (highRank == 1) {
                basicPay = 2;
            } else if (highRank == 2) {
                basicPay = 1;
            }
        } else if (highDiff == 0) {
            basicPay = 1;
        }
        if (c > t) {
            c = t;
        }
        int commission = 500 * (t - c);
        int totalItem = (basicPay * 3) - (c * basicPay);
        //#r배고픈 무토#k를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b500 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i1712002:# #t1712002:# 15개#k는 자네에게 주겠네.
        if (1 == self.askAccept("#r배고픈 무토#k를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b" + commission + " 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i1712002:# #t1712002:# " + totalItem + "개#k는 자네에게 주겠네.", ScriptMessageFlag.Scenario)) {
            if (getPlayer().getMaplePoints() >= commission) {
                if (target.exchange(1712002, totalItem) > 0) {
                    while (getPlayer().GetCount("hungry_muto") < 3) {
                        getPlayer().CountAdd("hungry_muto");
                    }
                    getPlayer().updateOneInfo(39052, "c1", String.valueOf(t));
                    getPlayer().setMaplePoint(getPlayer().getMaplePoints() - commission);
                    self.say("완료되었네\r\n크크.. 또 이용해 주시게나.", ScriptMessageFlag.Scenario);
                } else {
                    self.say("인벤토리가 부족하군\r\n인벤토리를 비운 뒤 다시 이용해주게나");
                }
            } else {
                self.say("자네의 메이플 포인트가 부족한 것 같군 다시 확인해보시게나");
            }
        }
    }

    public void arcaneRiverQuickPath12() { //드림브레이커
        int best = getPlayer().getOneInfoQuestInteger(15901, "best");
        if (best <= 0) {
            self.sayOk("#r드림브레이커#k에서 자네의 최고 성과를 찾을 수 없는 것 같네.\r\n그걸 기준으로 일을 처리해야 공평하지 않겠나? 크크...\r\n\r\n(#b아케인리버 퀵패스#k 이용 전 최초 1회의 해당 컨텐츠 플레이가 필요합니다.)", ScriptMessageFlag.Scenario);
            return;
        }
        int t = 3;
        if (getPlayer().getLevel() >= 225)
            t--;
        if (getPlayer().getLevel() >= 230)
            t--;
        int c = getPlayer().GetCount("dream_breaker");

        int basicPay = getPlayer().getOneInfoQuestInteger(15901, "best");

        if (c > t) {
            c = t;
        }
        int commission = 500 * (t - c);
        int totalItem = (basicPay * 3) - (c * basicPay);
        int itemId = 4310227;
        if (1 == self.askAccept("#r드림브레이커#k를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b" + commission + " 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i4310227:# #t4310227:# " + totalItem + "개#k는 자네에게 주겠네.\r\n\r\n(※ #r주의#k : 퀵패스를 통해 완료 시 힘내라! 보너스 코인 지급과 랭킹 기록은 되지 않습니다.)", ScriptMessageFlag.Scenario)) {
            if (getPlayer().getMaplePoints() >= commission) {
                if (target.exchange(itemId, totalItem) > 0) {
                    while (getPlayer().GetCount("dream_breaker") < 3) {
                        getPlayer().CountAdd("dream_breaker");
                    }
                    getPlayer().updateOneInfo(39052, "c2", String.valueOf(t));
                    getPlayer().setMaplePoint(getPlayer().getMaplePoints() - commission);
                    self.say("완료되었네\r\n크크.. 또 이용해 주시게나.", ScriptMessageFlag.Scenario);
                } else {
                    self.say("인벤토리가 부족하군\r\n인벤토리를 비운 뒤 다시 이용해주게나");
                }
            } else {
                self.say("자네의 메이플 포인트가 부족한 것 같군 다시 확인해보시게나");
            }
        }
    }

    public void arcaneRiverQuickPath13() { //스피릿세이비어

        int t = 3;
        if (getPlayer().getLevel() >= 230)
            t--;
        if (getPlayer().getLevel() >= 235)
            t--;
        int c = getPlayer().getOneInfoQuestInteger(16214, "count");

        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(16214, "date"));
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
        }
        if (lastTime != null) {
            if (!lastTime.equals(now)) {
                c = 0;
            }
        }
        int basicPay = 10;

        if (c > t) {
            c = t;
        }
        int commission = 500 * (t - c);
        int totalItem = (basicPay * 3) - (c * basicPay);
        int itemId = 4310235;
        if (1 == self.askAccept("#r스피릿 세이비어#k를 처리해주면 되겠나?\r\n\r\n의뢰금은 #b" + commission + " 메이플포인트#k라네.\r\n부가적으로 얻게될 #b#i4310235:# #t4310235:# " + totalItem + "개#k는 자네에게 주겠네.\r\n자네는 오늘 #r#e30개 이상#n#k의 스피릿 코인을 받아서 이만큼이 최선이라고 하던데? 크크..\r\n\r\n\r\n(※ #r주의#k : 퀵패스를 통해 완료 시 보너스 코인 지급은 되지 않습니다.)", ScriptMessageFlag.Scenario)) {
            if (getPlayer().getMaplePoints() >= commission) {
                if (target.exchange(itemId, totalItem) > 0) {
                    getPlayer().updateOneInfo(16214, "date", sdf.format(new Date()));
                    getPlayer().updateOneInfo(16214, "count", "3");
                    getPlayer().updateOneInfo(39052, "c3", String.valueOf(t));
                    getPlayer().setMaplePoint(getPlayer().getMaplePoints() - commission);
                    self.say("완료되었네\r\n크크.. 또 이용해 주시게나.", ScriptMessageFlag.Scenario);
                } else {
                    self.say("인벤토리가 부족하군\r\n인벤토리를 비운 뒤 다시 이용해주게나");
                }
            } else {
                self.say("자네의 메이플 포인트가 부족한 것 같군 다시 확인해보시게나");
            }
        }
    }

    public byte[] arcaneRiverQuickPath(boolean available) {
        final PacketEncoder p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.ARCANERIVER_QUICK_PATH.getValue());
        p.writeInt(available ? 1 : 0);
        return p.getPacket();
    }
}
