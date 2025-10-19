package script.ArcaneRiver;


import constants.ServerConstants;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

public class LabyrinthofSuffering extends ScriptEngineNPC {

    public void q35740s() {
        target.say("고통의 미궁에서 환영이 아닌 #b진 힐라를 물리치자.#k", ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
        getQuest().forceStart(getPlayer(), getNpc().getId(), "");
    }

    public void q35740e() {
        target.say("앗, 시그너스님께서 연락이..?", ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
        initNPC(MapleLifeFactory.getNPC(3003750));
        if (1 == self.askAccept("#face0#고통의 미궁을 정복하고 #b진 힐라#k를 물리치신 #h0#님...\r\n이 물건이 당신의 공적을 더 빛내줄 거예요.\r\n\r\n#i1143136# #r#t1143136#", ScriptMessageFlag.Scenario)) {
            if (target.exchange(1143136, 1) > 0) {
                getQuest().forceComplete(getPlayer(), getNpc().getId());
            } else {
                self.say("#face0#장비창을 1칸이상 비워 주신 뒤 다시 말을 걸어 주세요.", ScriptMessageFlag.Scenario);
            }
        }
    }

    @Script
    public void go_MoonBridge() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450009300, 1);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void west_450011320() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011600, 1);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void west_450011220() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011530, 2);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void west_450011510() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011500, 2);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_450011500() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011510, 1);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450011420() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011120, 2);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void south_450011220() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011590, 0);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_nextMaze3() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011320, 3);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void east_450011320() {
        initNPC(MapleLifeFactory.getNPC(9001000));
        if (getPlayer().getLevel() >= 250) {
            String text = "이질적인 기운이 느껴진다. 어느 곳으로 갈까?\r\n\r\n#b#L0#욕망의 제단 입구#l";
            if (getPlayer().getLevel() >= 255) {
                text += "\r\n#L1#리멘-세계의 눈물#l";
            }
            int v = target.askMenu(text, ScriptMessageFlag.Scenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage);
            switch (v) {
                case 0: { //욕망의 제단 입구
                    registerTransferField(450011990, 1);
                    break;
                }
                case 1: { //리멘 세계의 눈물
                    if (getPlayer().getLevel() >= 255) {
                        registerTransferField(450012000, 0);
                    }
                    break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void east_450011220() {
        if (getPlayer().getLevel() >= 250) {
            registerTransferField(450011540, 1);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_altarIn() {
        if (getPlayer().getLevel() >= 250) {
            getPlayer().updateOneInfo(450011580, "altar", getPlayer().getMap().getId() + "");
            registerTransferField(450011580, 0);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    @Script
    public void pt_altarOut() {
        if (getPlayer().getLevel() >= 250) {
            int mapid = getPlayer().getOneInfoQuestInteger(450011580, "altar");
            registerTransferField(mapid, 0);
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }

    public void BPReturn_JinHill() {
        initNPC(MapleLifeFactory.getNPC(9001000));
        if (getPlayer().getLevel() >= 250) {
            int v = target.askMenu("어디로 갈까?\r\n#b#L0#광장으로#l\r\n#L1#고통의 미궁 최심부 거점#l");
            switch (v) {
                case 0: { //광장
                    registerTransferField(ServerConstants.TownMap);
                    break;
                }
                case 1: { //세계의 눈물 중단3
                    registerTransferField(450011320, 7);
                    break;
                }
            }
        } else {
            getPlayer().dropMessage(5, "250레벨 이상만 입장하실 수 있습니다.");
        }
    }
}
