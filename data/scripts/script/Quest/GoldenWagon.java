package script.Quest;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.context.GoldenChariot;
import objects.item.MapleInventoryType;
import objects.quest.MapleQuest;
import objects.utils.HexTool;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import database.DBConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class GoldenWagon extends ScriptEngineNPC {
    public void q100750s() {
        if (getPlayer().getQuestStatus(100748) == 0) {
            MapleQuest.getInstance(100748).forceComplete(getPlayer(), 2007);
        }

        PacketEncoder p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        p.write(14);
        p.writeInt(252);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        String count = getPlayer().getOneInfoQuest(1234699, "count");
        String qex = "count=" + count + ";T=" + time;
        p.writeMapleAsciiString(qex);

        getPlayer().send(p.getPacket());

        p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        p.write(14);
        p.writeInt(253);
        StringBuilder builder = new StringBuilder();
        builder.append("complete=")
                .append(getPlayer().getOneInfo(1234699, "complete"))
                .append(";")
                .append("day=")
                .append(getPlayer().getOneInfo(1234699, "day"))
                .append(";")
                .append("passCount=")
                .append(getPlayer().getOneInfo(1234699, "passCount"))
                .append(";")
                .append("bMaxDay=91;")
                .append("lastDate=")
                .append(getPlayer().getOneInfo(1234699, "lastDate"))
                .append(";")
                .append("cMaxDay=91");
        p.writeMapleAsciiString(builder.toString());
        getPlayer().send(p.getPacket());

        p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        p.write(14);
        p.writeInt(254);
        p.writeMapleAsciiString("season=2021/02");
        getPlayer().send(p.getPacket());

        p = new PacketEncoder();
        p.writeShort(SendPacketOpcode.UI_EVENT_SET.getValue());
        p.writeInt(100748);
        p.writeInt(1254);
        getPlayer().send(p.getPacket());

        PacketEncoder s = new PacketEncoder();
        s.writeShort(SendPacketOpcode.UI_EVENT_INFO.getValue());

        s.writeInt(100748); //qID
        s.write(1);
        s.write(0);
        s.encodeBuffer(HexTool.getByteArrayFromHexString("00 C0 ED 28 09 0B D7 01")); //2021-02-25
        s.encodeBuffer(HexTool.getByteArrayFromHexString("80 29 99 B6 0B 63 D7 01")); //2021-06-16
        s.writeInt(126);
        s.writeInt(100748);
        s.writeInt(0);
        s.write(0);
        s.writeInt(0);
        s.writeInt(252); //WorldShareQID 1
        s.writeInt(253); //WorldShareQID 2
        s.writeInt(254); //WorldShareQID 3
        s.writeInt(3600); //총 채워야하는 초
        s.writeMapleAsciiString("chariotInfo4");
        s.writeMapleAsciiString("");
        s.writeMapleAsciiString("chariotPass4");
        s.writeMapleAsciiString("chariotAttend4");
        s.writeInt(0);
        s.writeInt(GoldenChariot.goldenChariotList.size());

        for (GoldenChariot gc : GoldenChariot.goldenChariotList) {
            s.writeInt(gc.getDay());
            int itemId = gc.getItemID();
            if (itemId == 1672077) {
                itemId = 4000000;
            }
            s.writeInt(itemId);
            s.writeInt(0);
            s.write(0);
            s.writeInt(0);
            s.write(0);
            s.writeInt(0);
            s.writeInt(0);
            s.write(0);
        }
        s.writeInt(0);
        s.writeInt(1254);
        getPlayer().send(s.getPacket());
    }

    public void goldenchariot() {
        if (DBConfig.isGanglim) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Date StartDate = dateFormat.parse("2025-05-31");
                Date EndDate = dateFormat.parse("2025-12-31");
                String Today = dateFormat.format(new Date());
                String Start = dateFormat.format(StartDate);
                String End = dateFormat.format(EndDate);
                Date TodateDate =  dateFormat.parse(Today);

                if (TodateDate.before(StartDate) || TodateDate.after(EndDate)){
                    self.sayOk("#fs11#황금마차 이벤트 진행기간이 아닙니다\r\n\r\n진행기간 : " + Start + " ~ " + End, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    return;
                }
            } catch (ParseException ex) {
            }
        }

        boolean isWeekend = (new Date().getDay() == 0 || new Date().getDay() == 6);
        if (!getPlayer().getOneInfoQuest(1234699, "complete").equals("1")) {
            if (!isWeekend) {
                if ((getPlayer().getOneInfoQuestInteger(1234699, "day") + 1) % 7 != 0) {
                    getPlayer().updateOneInfo(1234699, "complete", "1");
                    getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 1));
                    PacketEncoder p = new PacketEncoder();
                    p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                    p.write(14);
                    p.writeInt(253);
                    String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                    p.writeMapleAsciiString(qex);
                    getPlayer().send(p.getPacket());
                    self.sayOk("출석 완료! 도장을 #b1개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                } else { //상품받는날짜
                    GoldenChariot gc = null;
                    for (GoldenChariot g : GoldenChariot.goldenChariotList) {
                        if (g.getDay() == getPlayer().getOneInfoQuestInteger(1234699, "day") + 1) {
                            gc = g;
                            break;
                        }
                    }
                    if (self.askYesNo("지금 바로 출석을 완료하고 아래 선물을 받아 갈래?\r\n\r\n#e#b#i" + gc.getItemID() + "# #z" + gc.getItemID() + "#", ScriptMessageFlag.NpcReplacedByNpc) == 1) {
                        if (GameConstants.isPet(gc.getItemID())) {
                            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                                exchangePetPeriod(gc.getItemID(), 90);
                                getPlayer().updateOneInfo(1234699, "complete", "1");
                                getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 1));
                                PacketEncoder p = new PacketEncoder();
                                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                                p.write(14);
                                p.writeInt(253);
                                String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                                p.writeMapleAsciiString(qex);
                                getPlayer().send(p.getPacket());
                                self.sayOk("출석 완료! 도장을 #b1개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            } else {
                                self.sayOk("아이템을 받을 공간이 부족해! 공간을 비운 뒤 다시 시도 해줘", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            }
                        } else {
                            if (target.exchange(gc.getItemID(), gc.getItemQty()) > 0) {
                                getPlayer().updateOneInfo(1234699, "complete", "1");
                                getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 1));
                                PacketEncoder p = new PacketEncoder();
                                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                                p.write(14);
                                p.writeInt(253);
                                String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                                p.writeMapleAsciiString(qex);
                                getPlayer().send(p.getPacket());
                                self.sayOk("출석 완료! 도장을 #b1개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            } else {
                                self.sayOk("아이템을 받을 공간이 부족해! 공간을 비운 뒤 다시 시도 해줘", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            }
                        }
                    }
                }
            } else { //주말
                boolean isGiftDay = false;
                int gcDay = 0;
                for (int i=1; i<=2; i++) {
                    if ((getPlayer().getOneInfoQuestInteger(1234699, "day") + i) % 7 == 0) {
                        gcDay = getPlayer().getOneInfoQuestInteger(1234699, "day") + i;
                        isGiftDay = true;
                        break;
                    }
                }
                if (!isGiftDay) {
                    getPlayer().updateOneInfo(1234699, "complete", "1");
                    getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 2));
                    PacketEncoder p = new PacketEncoder();
                    p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                    p.write(14);
                    p.writeInt(253);
                    String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                    p.writeMapleAsciiString(qex);
                    getPlayer().send(p.getPacket());
                    self.sayOk("출석 완료! 주말이라서 도장을 #b2개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                } else { //상품받는날짜
                    GoldenChariot gc = null;
                    for (GoldenChariot g : GoldenChariot.goldenChariotList) {
                        if (g.getDay() == gcDay) {
                            gc = g;
                            break;
                        }
                    }
                    if (self.askYesNo("지금 바로 출석을 완료하고 아래 선물을 받아 갈래?\r\n\r\n#e#b#i" + gc.getItemID() + "# #z" + gc.getItemID() + "#", ScriptMessageFlag.NpcReplacedByNpc) == 1) {
                        if (GameConstants.isPet(gc.getItemID())) {
                            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                                exchangePetPeriod(gc.getItemID(), 90);
                                getPlayer().updateOneInfo(1234699, "complete", "1");
                                getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 2));
                                PacketEncoder p = new PacketEncoder();
                                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                                p.write(14);
                                p.writeInt(253);
                                String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                                p.writeMapleAsciiString(qex);
                                getPlayer().send(p.getPacket());
                                self.sayOk("출석 완료! 주말이라서 도장을 #b2개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            } else {
                                self.sayOk("아이템을 받을 공간이 부족해! 공간을 비운 뒤 다시 시도 해줘", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            }
                        } else {
                            if (target.exchange(gc.getItemID(), gc.getItemQty()) > 0) {
                                getPlayer().updateOneInfo(1234699, "complete", "1");
                                getPlayer().updateOneInfo(1234699, "day", "" + (getPlayer().getOneInfoQuestInteger(1234699, "day") + 2));
                                PacketEncoder p = new PacketEncoder();
                                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                                p.write(14);
                                p.writeInt(253);
                                String qex = "complete=1;day=" + getPlayer().getOneInfo(1234699, "day") + ";" + "passCount=0;bMaxDay=91;lastDate=" + getPlayer().getOneInfo(1234699, "lastDate") + ";cMaxDay=91";
                                p.writeMapleAsciiString(qex);
                                getPlayer().send(p.getPacket());
                                self.sayOk("출석 완료! 주말이라서 도장을 #b2개#k 찍어 줬어!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            } else {
                                self.sayOk("아이템을 받을 공간이 부족해! 공간을 비운 뒤 다시 시도 해줘", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                            }
                        }
                    }
                }
            }
        }
    }

    public void goldenchariotWIG() {
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n#b페어리 브로의 황금마차#k는 매일 출석 도장을 쾅쾅! 찍고 선물을 잔뜩 받아가는 신나는 이벤트야!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n이벤트 기간 동안 게임에 접속하면 이벤트 알림이 버튼을 눌러 #b#e<페어리 브로의 황금마차>#k#n 출석판을 열어볼 수 있지!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n이벤트 기간 동안에는 매일 자동으로 접속 시간이 누적되고 #b60분#k이 가득 차면 출석 도장을 쾅! 찍을 수 있어!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n참고로 #b주말에는 한 번에 두 개#k의 출석 도장이 찍히니까 절대 놓치지 말라고!", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n출석 일차에 따른 선물은 다음과 같아!", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e<9회 출석 완료>\r\n#b#i2633063:# #t2633063:##k#n\r\n\r\n\r\n#e<18회 출석 완료>\r\n#b#i2633064:# #t2633064:##k#n\r\n\r\n\r\n#e<27회 출석 완료>\r\n#b#i2631708:# #t2631708:##k#n\r\n\r\n\r\n#e<36회 출석 완료>\r\n#b#i2633065:# #t2633065:##k#n\r\n\r\n\r\n#e<45회 출석 완료>\r\n#b#i2633066:# #t2633066:##k#n\r\n\r\n\r\n#e<54회 출석 완료>\r\n#b#i2633067:# #t2633067:##k#n\r\n\r\n\r\n#e<63회 출석 완료>\r\n#b#i2631139:# #t2631139:##k#n\r\n\r\n\r\n#e<72회 출석 완료>\r\n#b#i2633071:# #t2633071:##k#n\r\n\r\n\r\n#e<81회 출석 완료>\r\n#b#i2633069:# #t2633069:##k#n\r\n\r\n\r\n#e<90회 출석 완료>\r\n#b#i2633070:# #t2633070:##k#n\r\n\r\n\r\n#e<99회 출석 완료>\r\n#b#i2633068:# #t2633068:##k#n\r\n\r\n\r\n#e<108회 출석 완료>\r\n#b#i2633072:# #t2633072:##k#n\r\n\r\n\r\n#e<117회 출석 완료>\r\n#b#i2633073:# #t2633073:##k#n\r\n\r\n\r\n#e<126회 출석 완료>\r\n#b#i2631717:# #t2631717:##k#n", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n깜빡하고 출석을 놓쳤을 때를 대비한 특별 찬스!\r\n#b#e<골든패스>#k#n에 대해서도 알려 줄게!", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n출석판의 #b#e<골든패스>#k#n를 사용하면 #b3천 메이플 포인트#k를 소모하고 미처 #b완료하지 못한 출석#k 도장을 한 번 찍을 수가 있어.", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n단, 골든패스의 #b사용 가능 수량#k은 #r하루 전 날짜 기준 완료하지 못한 일차 수#k만큼으로 제한되는 점 알아 둬!", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n골든패스는 사용 #r요일과 상관없이 하나의 도장#k만 찍어준다는 사실도 잊지 말고 말이야!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n아차! #e접속 시간 누적#n과 #e보상 획득 기록#n은 #r메이플ID 단위#k로 진행되고, #r101레벨 이상의 캐릭터#k만 접속 시간을 쌓을 수 있다는 것도 유념해 둬!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n그리고 날짜가 변경될 때 다른 행동을 하고 있다면 접속 시간이 정상적으로 쌓이지 않을 수 있으니까, #e날짜가 바뀐 다음에는 출석판을 열어 접속 시간이 잘 쌓이고 있는지 한 번 확인#n해 보도록 해!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n더 자세한 내용은 공식 홈페이지를 참고해 봐!", ScriptMessageFlag.NpcReplacedByNpc);
        //self.say("#e#b<페어리 브로의 황금마차>#k#n\r\n\r\n\r\n#e※ 이벤트 기간#n\r\n  - 2021년 #r6월 16일(수) 오후 11시 59분#k까지#k", ScriptMessageFlag.NpcReplacedByNpc);
    }

    public void goldenchariotPass() {
        //self.sayOk("#b골든패스#k를 사용하려면 #r3천 메이플포인트#k가 필요해!", ScriptMessageFlag.NpcReplacedByNpc);
        self.sayOk("#fs11#지금은 패스 기능을 사용할 수 없습니다.", ScriptMessageFlag.NpcReplacedByNpc);
    }
}
