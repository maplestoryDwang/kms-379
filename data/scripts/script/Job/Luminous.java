package script.Job;

import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Luminous extends ScriptEngineNPC {

    public void luminous_type() {
        initNPC(MapleLifeFactory.getNPC(2007));
        /*
         * 1F B1 1F 00
         * 03
         * 00 00 00 00
         * 00
         * 19
         * 01 00
         * 00
         *
         * 00 00 00 00
         *
         * 00 00 00 00
         * 01
         * 00 00 00 00
         *
         * 02 00 00 00
         *
         * 07 00 BA FB C0 C7 20 B1 E6
         * 09 00 BE EE B5 D2 C0 C7 20 B1 E6
         */

        int menu = self.askSelectMenu(List.of("빛의 길", "어둠의 길"), 0x1, ScriptMessageFlag.NoEsc);

        /*
        cm.teachSkill(27001201, 20, 20);
            cm.teachSkill(27000207, 5, 5);
        } else if (type_ == 1) {
            cm.teachSkill(27001100, 20, 20);
            cm.teachSkill(27000106, 5, 5);
         */

        Map<Skill, SkillEntry> skillEntryMap = new HashMap<>();
        int route = -1;
        if (menu == 0) {
            //어둠 -> 빛
            skillEntryMap.put(SkillFactory.getSkill(27001201), new SkillEntry(-1, (byte) 20, -1));
            skillEntryMap.put(SkillFactory.getSkill(27000207), new SkillEntry(-1, (byte) 5, -1));

            skillEntryMap.put(SkillFactory.getSkill(27001100), new SkillEntry(20, (byte) 20, -1));
            skillEntryMap.put(SkillFactory.getSkill(27000106), new SkillEntry(5, (byte) 5, -1));

            route = 1;
        } else if (menu == 1) {
            //빛 -> 어둠
            skillEntryMap.put(SkillFactory.getSkill(27001100), new SkillEntry(-1, (byte) 20, -1));
            skillEntryMap.put(SkillFactory.getSkill(27000106), new SkillEntry(-1, (byte) 5, -1));

            skillEntryMap.put(SkillFactory.getSkill(27001201), new SkillEntry(20, (byte) 20, -1));
            skillEntryMap.put(SkillFactory.getSkill(27000207), new SkillEntry(5, (byte) 5, -1));

            route = 0;
        }

        getPlayer().send(CWvsContext.enableActions(getPlayer(), false));
        if (!skillEntryMap.isEmpty()) {
            getPlayer().changeSkillsLevel(skillEntryMap);
            getPlayer().updateOneInfo(25505, "route", String.valueOf(route));
            getPlayer().updateOneInfo(25505, "skill4", String.valueOf(getPlayer().getOneInfoQuestInteger(25505, "skill4") + 1));
            getPlayer().send(CWvsContext.InfoPacket.brownMessage("새로운 운명을 선택했습니다."));
        }
    }
}
