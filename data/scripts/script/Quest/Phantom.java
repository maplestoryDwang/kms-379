package script.Quest;

import constants.GameConstants;
import objects.users.skills.SkillFactory;
import scripting.newscripting.ScriptEngineNPC;
import scripting.NPCScriptManager;

public class Phantom extends ScriptEngineNPC {

    public void phantomskill() {
        if (getPlayer().getLevel() >= 100) {
            //getClient().removeClickedNPC();
            //NPCScriptManager.getInstance().start(getClient(), 1052206, "보스이동", true);
            //return;
            self.sayOk("#fs11#현재는 사용할 수 없는 명령어야");
            return;
        }

        if (!GameConstants.isPhantom(getPlayer().getJob())) {
            self.sayOk("#fs11#너는 팬텀이 아니라서 사용할 수 없는 기능이야");
            return;
        }
        String steal = "#fs11##e[1차 스킬]#n\r\n";
        if (getPlayer().getLevel() >= 10) {
            //1차만
            steal += "#L1# #s4001003# 다크사이트#l\r\n";
            steal += "#L2# #s3011004# 카디널 디스차지#l\r\n";
            steal += "#L3# #s2001002# 매직 가드#l\r\n";
        }
        if (getPlayer().getLevel() >= 30) {

            steal += "\r\n\r\n#e[2차 스킬]#n\r\n";
            steal += "#L4# #s3301003# 카디널 블래스트#l\r\n";
            steal += "#L5# #s2301002# 힐#l\r\n";
            steal += "#L6# #s1101006# 스피릿 블레이드#l\r\n";
            //2차까지
        }
        if (getPlayer().getLevel() >= 60) {
            steal += "\r\n\r\n#e[3차 스킬]#n\r\n";
            steal += "#L8# #s3111013# 애로우 플래터#l\r\n";
            steal += "#L9# #s1311015# 크로스 오버 체인#l\r\n";
            steal += "#L10# #s4331011# 블레이드 어센션#l\r\n";
            steal += "#L11# #s4331006# 사슬지옥#l\r\n";
            steal += "#L12# #s2311009# 홀리 매직쉘#l\r\n";
            steal += "#L13# #s2311003# 홀리 심볼#l\r\n";
            steal += "#L14# #s2311011# 홀리 파운틴#l\r\n";
            //3차까지
        }
        if (getPlayer().getLevel() >= 100){
            //4차까지
            steal += "\r\n\r\n#e[4차 스킬]#n\r\n";
            steal += "#L15# #s4341002# 파이널 컷#l\r\n";
            steal += "#L16# #s1221011# 생츄어리#l\r\n";
            steal += "#L17# #s2221012# 프로즌 오브#l\r\n";
            steal += "#L18# #s2221011# 프리징 브레스#l\r\n";
            steal += "#L19# #s5321000# 캐논 바주카#l\r\n";
            steal += "#L20# #s5121010# 타임 리프#l\r\n";
        }

        if (getPlayer().getLevel() >= 140){
            steal += "\r\n#e[하이퍼 스킬]#n\r\n";
            steal += "#L21# #s3221054# 불스아이#l\r\n";
            steal += "#L22# #s1221054# 새크로생티티#l\r\n";
            steal += "#L23# #s3121054# 프리퍼레이션#l\r\n";
        }
        int v = self.askMenu(steal);
        if (getPlayer().getJob() == 2400) { //팬텀1차
            if (v > 3) {
                self.sayOk("#fs11#잘못된 요청입니다.");
                return;
            }
        } else if (getPlayer().getJob() == 2410) { //팬텀2차
            if (v > 7) {
                self.sayOk("#fs11#잘못된 요청입니다.");
                return;
            }
        } else if (getPlayer().getJob() == 2411) { //팬텀3차
            if (v > 14) {
                self.sayOk("#fs11#잘못된 요청입니다.");
                return;
            }
        } else if (getPlayer().getJob() == 2412) { //팬텀4차
            if (getPlayer().getLevel() < 140) {
                if (v > 21) {
                    self.sayOk("#fs11#잘못된 요청입니다.");
                    return;
                }
            }
        }
        int skillId = 0;
        switch (v) {
            case 1: //다크사이트
                skillId = 4001003;
                break;
            case 2:
                skillId = 3011004;
                break;
            case 3:
                skillId = 2001002;
                break;
            case 4:
                skillId = 3301003;
                break;
            case 5:
                skillId = 2301002;
                break;
            case 6:
                skillId = 1101006;
                break;
            case 8:
                skillId = 3111013;
                break;
            case 9:
                skillId = 1311015;
                break;
            case 10:
                skillId = 4331011;
                break;
            case 11:
                skillId = 4331006;
                break;
            case 12:
                skillId = 2311009;
                break;
            case 13:
                skillId = 2311003;
                break;
            case 14:
                skillId = 2311011;
                break;
            case 15:
                skillId = 4341002;
                break;
            case 16:
                skillId = 1221011;
                break;
            case 17:
                skillId = 2221012;
                break;
            case 18:
                skillId = 2221011;
                break;
            case 19:
                skillId = 5321000;
                break;
            case 20:
                skillId = 5121010;
                break;
            case 21:
                skillId = 3221054;
                break;
            case 22:
                skillId = 1221054;
                break;
            case 23:
                skillId = 3121054;
                break;
        }
        int maxLevel = SkillFactory.getSkill(skillId).getMaxLevel();
        if (maxLevel < 1) {
            self.sayOk("#fs11#요청하는동안 오류가 발생했습니다.");
            return;
        }
        getPlayer().addStolenSkill(skillId, maxLevel);
        //self.sayOk("성공했습니다. 스킬 매니저먼트를 확인해보세요.");
    }
}
