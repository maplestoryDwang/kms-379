package script.Boss;

import database.DBConfig;
import objects.fields.fieldset.childs.HardBlackMageEnter;
import objects.fields.fieldset.childs.HardJinHillahEnter;
import objects.fields.fieldset.childs.NormalJinHillahEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.users.MapleCharacter;

public class BlackMage extends ScriptEngineNPC {

    public void bossBlackMage_pt() {
        initNPC(MapleLifeFactory.getNPC(2007));
        String Message = "為了對抗黑魔法師，要不要前往#b黑暗神殿#k？ \r\n\r\n";
        if (DBConfig.isGanglim) {
            Message += "前往#L0#黑暗神殿（#b困難模式#k）。 #r（255級以上）#g[" + getPlayer().getOneInfoQuestInteger(1234570, "blackmage_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
            Message += "前往#L1#黑暗神殿（#b困難練習模式#k）。 #r（255級以上）#k#l\r\n\r\n";
            Message += "#L2#不移動。 #l";
        } else {
            Message += "#L0##b前往黑暗神殿。 #k#l\r\n";
            Message += "#L1##b前往黑暗神殿（練習模式）。 #k#l\r\n\r\n";
            Message += "#L2#不移動。 #l";
        }
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu == 2) return; //이동하지 않는다
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("所有隊員必須在同一地圖上。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        switch (Menu) { //따로 제한되는거 없으면 바로 입장가능함
            case 0: { //하드모드
                HardBlackMageEnter fieldSet = (HardBlackMageEnter) fieldSet("HardBlackMageEnter");
                int enter = fieldSet.enter(target.getId(), false, 7);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -3) {
                    self.sayOk("有未達到入場限制次數的隊員，無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -4) {
                    self.sayOk("入場資料不足的隊員無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == -1 || enter == 4)) {
                    self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -2) {
                    self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
            case 1: { //하드 연습 모드
                int practiceMode = self.askYesNo("已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#n#k BOSS怪物的種類如何，每天只能使用#b#e 20次#n#k。 您要入場嗎？");
                if (practiceMode == 0) {
                    return;
                }
                HardBlackMageEnter fieldSet = (HardBlackMageEnter) fieldSet("HardBlackMageEnter");
                int enter = fieldSet.enter(target.getId(), true, 7);
                if (enter == 6) {
                    self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -5) {
                    self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if ((enter == 4)) {
                    self.sayOk("有等級限制不符的隊員，無法進入。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                if (enter == -1) {
                    self.sayOk("練習模式每天只能進行20次。", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                break;
            }
        }
    }

    public void bmbossfield_out() {
        if (this.npc == null) {
            initNPC(MapleLifeFactory.getNPC(3005411));
        }
        if (self.askYesNo("你要停止戰鬥出去嗎？") == 1) {
            registerTransferField(450012500, 1);
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            if (getPlayer().getBossMode() == 1) {
                getPlayer().setBossMode(0);
            }
        }
    }
}
