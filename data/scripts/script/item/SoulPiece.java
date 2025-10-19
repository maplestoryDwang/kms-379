package script.item;

import objects.context.SpecialSunday;
import objects.utils.Randomizer;
import scripting.newscripting.ScriptEngineNPC;

import java.util.*;

public class SoulPiece extends ScriptEngineNPC {

    public void consume_2431655() { //락 스피릿의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591010, 15.0d); //기운찬 락 스피릿의 소울
        percentageInfo.put(2591011, 15.0d); //날렵한 락 스피릿의 소울
        percentageInfo.put(2591012, 15.0d); //총명한 락 스피릿의 소울
        percentageInfo.put(2591013, 15.0d); //놀라운 락 스피릿의 소울
        percentageInfo.put(2591014, 15.0d); //강인한 락 스피릿의 소울
        percentageInfo.put(2591015, 15.0d); //풍부한 락 스피릿의 소울
        percentageInfo.put(2591016, 10.0d); //화려한 락 스피릿의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431656() { //교도관 아니의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591017, 15.0d); //기운찬 교도관 아니의 소울
        percentageInfo.put(2591018, 15.0d); //날렵한 교도관 아니의 소울
        percentageInfo.put(2591019, 15.0d); //총명한 교도관 아니의 소울
        percentageInfo.put(2591020, 15.0d); //놀라운 교도관 아니의 소울
        percentageInfo.put(2591021, 15.0d); //강인한 교도관 아니의 소울
        percentageInfo.put(2591022, 15.0d); //풍부한 교도관 아니의 소울
        percentageInfo.put(2591023, 10.0d); //화려한 교도관 아니의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431657() { //드래곤 라이더의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591024, 15.0d); //기운찬 드래곤 라이더의 소울
        percentageInfo.put(2591025, 15.0d); //날렵한 드래곤 라이더의 소울
        percentageInfo.put(2591026, 15.0d); //총명한 드래곤 라이더의 소울
        percentageInfo.put(2591027, 15.0d); //놀라운 드래곤 라이더의 소울
        percentageInfo.put(2591028, 15.0d); //강인한 드래곤 라이더의 소울
        percentageInfo.put(2591029, 15.0d); //풍부한 드래곤 라이더의 소울
        percentageInfo.put(2591030, 10.0d); //화려한 드래곤 라이더의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431658() { //렉스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591031, 15.0d); //기운찬 렉스의 소울
        percentageInfo.put(2591032, 15.0d); //날렵한 렉스의 소울
        percentageInfo.put(2591033, 15.0d); //총명한 렉스의 소울
        percentageInfo.put(2591034, 15.0d); //놀라운 렉스의 소울
        percentageInfo.put(2591035, 15.0d); //강인한 렉스의 소울
        percentageInfo.put(2591036, 15.0d); //풍부한 렉스의 소울
        percentageInfo.put(2591037, 10.0d); //화려한 렉스의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431659() { //무공의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591038, 15.0d); //기운찬 무공의 소울
        percentageInfo.put(2591039, 15.0d); //날렵한 무공의 소울
        percentageInfo.put(2591040, 15.0d); //총명한 무공의 소울
        percentageInfo.put(2591041, 15.0d); //놀라운 무공의 소울
        percentageInfo.put(2591042, 15.0d); //강인한 무공의 소울
        percentageInfo.put(2591043, 15.0d); //풍부한 무공의 소울
        percentageInfo.put(2591044, 10.0d); //화려한 무공의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431660() { //발록의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591045, 15.0d); //기운찬 발록의 소울
        percentageInfo.put(2591046, 15.0d); //날렵한 발록의 소울
        percentageInfo.put(2591047, 15.0d); //총명한 발록의 소울
        percentageInfo.put(2591048, 15.0d); //놀라운 발록의 소울
        percentageInfo.put(2591049, 15.0d); //화려한 발록의 소울
        percentageInfo.put(2591050, 8.0d); //강력한 발록의 소울
        percentageInfo.put(2591051, 8.0d); //빛나는 발록의 소울
        percentageInfo.put(2591052, 8.0d); //강인한 발록의 소울
        //percentageInfo.put(2591053, 1.5d); //날카로운 발록의 소울
        //percentageInfo.put(2591054, 1.5d); //파괴하는 발록의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591085, 5.0d); //위대한 발록의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591085, 1.0d); //위대한 발록의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431661() { //핑크빈의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591055, 15.0d); //기운찬 핑크빈의 소울
        percentageInfo.put(2591056, 15.0d); //날렵한 핑크빈의 소울
        percentageInfo.put(2591057, 15.0d); //총명한 핑크빈의 소울
        percentageInfo.put(2591058, 15.0d); //놀라운 핑크빈의 소울
        percentageInfo.put(2591059, 15.0d); //화려한 핑크빈의 소울
        percentageInfo.put(2591060, 8.0d); //강력한 핑크빈의 소울
        percentageInfo.put(2591061, 8.0d); //빛나는 핑크빈의 소울
        percentageInfo.put(2591062, 8.0d); //강인한 핑크빈의 소울
        //percentageInfo.put(2591063, 1.5d); //날카로운 핑크빈의 소울
        //percentageInfo.put(2591064, 1.5d); //파괴하는 핑크빈의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591087, 5.0d); //위대한 핑크빈의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591087, 1.0d); //위대한 핑크빈의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431662() { //반 레온의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591065, 15.0d); //기운찬 반 레온의 소울
        percentageInfo.put(2591066, 15.0d); //날렵한 반 레온의 소울
        percentageInfo.put(2591067, 15.0d); //총명한 반 레온의 소울
        percentageInfo.put(2591068, 15.0d); //놀라운 반 레온의 소울
        percentageInfo.put(2591069, 15.0d); //화려한 반 레온의 소울
        percentageInfo.put(2591070, 8.0d); //강력한 반 레온의 소울
        percentageInfo.put(2591071, 8.0d); //빛나는 반 레온의 소울
        percentageInfo.put(2591072, 8.0d); //강인한 반 레온의 소울
        //percentageInfo.put(2591073, 1.5d); //날카로운 반 레온의 소울
        //percentageInfo.put(2591074, 1.5d); //파괴하는 반 레온의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591086, 5.0d); //위대한 반 레온의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591086, 1.0d); //위대한 반 레온의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431709() { //크세르크세스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591148, 15.0d); //기운찬 크세르크세스의 소울
        percentageInfo.put(2591149, 15.0d); //날렵한 크세르크세스의 소울
        percentageInfo.put(2591150, 15.0d); //총명한 크세르크세스의 소울
        percentageInfo.put(2591151, 15.0d); //놀라운 크세르크세스의 소울
        percentageInfo.put(2591152, 15.0d); //강인한 크세르크세스의 소울
        percentageInfo.put(2591153, 15.0d); //풍부한 크세르크세스의 소울
        percentageInfo.put(2591154, 10.0d); //화려한 크세르크세스의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431710() { //자쿰의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591155, 15.0d); //기운찬 자쿰의 소울
        percentageInfo.put(2591156, 15.0d); //날렵한 자쿰의 소울
        percentageInfo.put(2591157, 15.0d); //총명한 자쿰의 소울
        percentageInfo.put(2591158, 15.0d); //놀라운 자쿰의 소울
        percentageInfo.put(2591159, 15.0d); //화려한 자쿰의 소울
        percentageInfo.put(2591160, 8.0d); //강력한 자쿰의 소울
        percentageInfo.put(2591161, 8.0d); //빛나는 자쿰의 소울
        percentageInfo.put(2591162, 8.0d); //강인한 자쿰의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591163, 5.0d); //위대한 자쿰의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591163, 1.0d); //위대한 자쿰의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431711() { //시그너스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591075, 15.0d); //기운찬 시그너스의 소울
        percentageInfo.put(2591076, 15.0d); //날렵한 시그너스의 소울
        percentageInfo.put(2591077, 15.0d); //총명한 시그너스의 소울
        percentageInfo.put(2591078, 15.0d); //놀라운 시그너스의 소울
        percentageInfo.put(2591079, 15.0d); //화려한 시그너스의 소울
        percentageInfo.put(2591080, 8.0d); //강력한 시그너스의 소울
        percentageInfo.put(2591081, 8.0d); //빛나는 시그너스의 소울
        percentageInfo.put(2591082, 8.0d); //강인한 시그너스의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591088, 5.0d); //위대한 시그너스의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591088, 1.0d); //위대한 시그너스의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431752() { //에피네아의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591187, 15.0d); //기운찬 에피네아의 소울
        percentageInfo.put(2591188, 15.0d); //날렵한 에피네아의 소울
        percentageInfo.put(2591189, 15.0d); //총명한 에피네아의 소울
        percentageInfo.put(2591190, 15.0d); //놀라운 에피네아의 소울
        percentageInfo.put(2591191, 15.0d); //강인한 에피네아의 소울
        percentageInfo.put(2591192, 15.0d); //풍부한 에피네아의 소울
        percentageInfo.put(2591193, 10.0d); //화려한 에피네아의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431753() { //아카이럼의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591194, 15.0d); //기운찬 아카이럼의 소울
        percentageInfo.put(2591195, 15.0d); //날렵한 아카이럼의 소울
        percentageInfo.put(2591196, 15.0d); //총명한 아카이럼의 소울
        percentageInfo.put(2591197, 15.0d); //놀라운 아카이럼의 소울
        percentageInfo.put(2591198, 15.0d); //화려한 아카이럼의 소울
        percentageInfo.put(2591199, 8.0d); //강력한 아카이럼의 소울
        percentageInfo.put(2591200, 8.0d); //빛나는 아카이럼의 소울
        percentageInfo.put(2591201, 8.0d); //강인한 아카이럼의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591202, 5.0d); //위대한 아카이럼의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591202, 1.0d); //위대한 아카이럼의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431895() { //피아누스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591218, 15.0d); //기운찬 피아누스의 소울
        percentageInfo.put(2591219, 15.0d); //날렵한 피아누스의 소울
        percentageInfo.put(2591220, 15.0d); //총명한 피아누스의 소울
        percentageInfo.put(2591221, 15.0d); //놀라운 피아누스의 소울
        percentageInfo.put(2591222, 15.0d); //강인한 피아누스의 소울
        percentageInfo.put(2591223, 15.0d); //풍부한 피아누스의 소울
        percentageInfo.put(2591224, 10.0d); //화려한 피아누스의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431896() { //힐라의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591225, 15.0d); //기운찬 힐라의 소울
        percentageInfo.put(2591226, 15.0d); //날렵한 힐라의 소울
        percentageInfo.put(2591227, 15.0d); //총명한 힐라의 소울
        percentageInfo.put(2591228, 15.0d); //놀라운 힐라의 소울
        percentageInfo.put(2591229, 15.0d); //화려한 힐라의 소울
        percentageInfo.put(2591230, 8.0d); //강력한 힐라의 소울
        percentageInfo.put(2591231, 8.0d); //빛나는 힐라의 소울
        percentageInfo.put(2591232, 8.0d); //강인한 힐라의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591233, 5.0d); //위대한 힐라의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591233, 1.0d); //위대한 힐라의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2431963() { //블랙 슬라임의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591249, 15.0d); //기운찬 블랙 슬라임의 소울
        percentageInfo.put(2591250, 15.0d); //날렵한 블랙 슬라임의 소울
        percentageInfo.put(2591251, 15.0d); //총명한 블랙 슬라임의 소울
        percentageInfo.put(2591252, 15.0d); //놀라운 블랙 슬라임의 소울
        percentageInfo.put(2591253, 15.0d); //강인한 블랙 슬라임의 소울
        percentageInfo.put(2591254, 15.0d); //풍부한 블랙 슬라임의 소울
        percentageInfo.put(2591255, 10.0d); //화려한 블랙 슬라임의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2431964() { //매그너스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591256, 15.0d); //기운찬 매그너스의 소울
        percentageInfo.put(2591257, 15.0d); //날렵한 매그너스의 소울
        percentageInfo.put(2591258, 15.0d); //총명한 매그너스의 소울
        percentageInfo.put(2591259, 15.0d); //놀라운 매그너스의 소울
        percentageInfo.put(2591260, 15.0d); //화려한 매그너스의 소울
        percentageInfo.put(2591261, 8.0d); //강력한 매그너스의 소울
        percentageInfo.put(2591262, 8.0d); //빛나는 매그너스의 소울
        percentageInfo.put(2591263, 8.0d); //강인한 매그너스의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591264, 5.0d); //위대한 매그너스의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591264, 1.0d); //위대한 매그너스의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2432138() { //무르무르의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591288, 15.0d); //기운찬 무르무르의 소울
        percentageInfo.put(2591289, 15.0d); //날렵한 무르무르의 소울
        percentageInfo.put(2591290, 15.0d); //총명한 무르무르의 소울
        percentageInfo.put(2591291, 15.0d); //놀라운 무르무르의 소울
        percentageInfo.put(2591292, 15.0d); //화려한 무르무르의 소울
        percentageInfo.put(2591293, 8.0d); //강력한 무르무르의 소울
        percentageInfo.put(2591294, 8.0d); //빛나는 무르무르의 소울
        percentageInfo.put(2591295, 8.0d); //강인한 무르무르의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591296, 5.0d); //위대한 무르무르의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591296, 1.0d); //위대한 무르무르의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2432575() { //모카딘의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591297, 15.0d); //기운찬 모카딘의 소울
        percentageInfo.put(2591298, 15.0d); //날렵한 모카딘의 소울
        percentageInfo.put(2591299, 15.0d); //총명한 모카딘의 소울
        percentageInfo.put(2591300, 15.0d); //놀라운 모카딘의 소울
        percentageInfo.put(2591301, 15.0d); //화려한 모카딘의 소울
        percentageInfo.put(2591302, 8.0d); //강력한 모카딘의 소울
        percentageInfo.put(2591303, 8.0d); //빛나는 모카딘의 소울
        percentageInfo.put(2591304, 8.0d); //강인한 모카딘의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591305, 5.0d); //위대한 모카딘의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591305, 1.0d); //위대한 모카딘의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2432576() { //카리아인의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591306, 15.0d); //기운찬 카리아인의 소울
        percentageInfo.put(2591307, 15.0d); //날렵한 카리아인의 소울
        percentageInfo.put(2591308, 15.0d); //총명한 카리아인의 소울
        percentageInfo.put(2591309, 15.0d); //놀라운 카리아인의 소울
        percentageInfo.put(2591310, 15.0d); //화려한 카리아인의 소울
        percentageInfo.put(2591311, 8.0d); //강력한 카리아인의 소울
        percentageInfo.put(2591312, 8.0d); //빛나는 카리아인의 소울
        percentageInfo.put(2591313, 8.0d); //강인한 카리아인의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591314, 5.0d); //위대한 카리아인의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591314, 1.0d); //위대한 카리아인의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2432577() { //CQ57의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591315, 15.0d); //기운찬 CQ57의 소울
        percentageInfo.put(2591316, 15.0d); //날렵한 CQ57의 소울
        percentageInfo.put(2591317, 15.0d); //총명한 CQ57의 소울
        percentageInfo.put(2591318, 15.0d); //놀라운 CQ57의 소울
        percentageInfo.put(2591319, 15.0d); //화려한 CQ57의 소울
        percentageInfo.put(2591320, 8.0d); //강력한 CQ57의 소울
        percentageInfo.put(2591321, 8.0d); //빛나는 CQ57의 소울
        percentageInfo.put(2591322, 8.0d); //강인한 CQ57의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591323, 5.0d); //위대한 CQ57의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591323, 1.0d); //위대한 CQ57의 소울
            change_soul_piece(percentageInfo);
        }

    }

    public void consume_2432578() { //줄라이의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591324, 15.0d); //기운찬 줄라이의 소울
        percentageInfo.put(2591325, 15.0d); //날렵한 줄라이의 소울
        percentageInfo.put(2591326, 15.0d); //총명한 줄라이의 소울
        percentageInfo.put(2591327, 15.0d); //놀라운 줄라이의 소울
        percentageInfo.put(2591328, 15.0d); //화려한 줄라이의 소울
        percentageInfo.put(2591329, 8.0d); //강력한 줄라이의 소울
        percentageInfo.put(2591330, 8.0d); //빛나는 줄라이의 소울
        percentageInfo.put(2591331, 8.0d); //강인한 줄라이의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591332, 5.0d); //위대한 줄라이의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591332, 1.0d); //위대한 줄라이의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2432579() { //플레드의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591333, 15.0d); //기운찬 플레드의 소울
        percentageInfo.put(2591334, 15.0d); //날렵한 플레드의 소울
        percentageInfo.put(2591335, 15.0d); //총명한 플레드의 소울
        percentageInfo.put(2591336, 15.0d); //놀라운 플레드의 소울
        percentageInfo.put(2591337, 15.0d); //화려한 플레드의 소울
        percentageInfo.put(2591338, 8.0d); //강력한 플레드의 소울
        percentageInfo.put(2591339, 8.0d); //빛나는 플레드의 소울
        percentageInfo.put(2591340, 8.0d); //강인한 플레드의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591341, 5.0d); //위대한 플레드의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591341, 1.0d); //위대한 플레드의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2433446() { //피에르의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591383, 15.0d); //기운찬 피에르의 소울
        percentageInfo.put(2591384, 15.0d); //날렵한 피에르의 소울
        percentageInfo.put(2591385, 15.0d); //총명한 피에르의 소울
        percentageInfo.put(2591386, 15.0d); //놀라운 피에르의 소울
        percentageInfo.put(2591387, 15.0d); //화려한 피에르의 소울
        percentageInfo.put(2591388, 8.0d); //강력한 피에르의 소울
        percentageInfo.put(2591389, 8.0d); //빛나는 피에르의 소울
        percentageInfo.put(2591390, 8.0d); //강인한 피에르의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591391, 5.0d); //위대한 피에르의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591391, 1.0d); //위대한 피에르의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2433515() { //반반의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591392, 15.0d); //기운찬 반반의 소울
        percentageInfo.put(2591393, 15.0d); //날렵한 반반의 소울
        percentageInfo.put(2591394, 15.0d); //총명한 반반의 소울
        percentageInfo.put(2591395, 15.0d); //놀라운 반반의 소울
        percentageInfo.put(2591396, 15.0d); //화려한 반반의 소울
        percentageInfo.put(2591397, 8.0d); //강력한 반반의 소울
        percentageInfo.put(2591398, 8.0d); //빛나는 반반의 소울
        percentageInfo.put(2591399, 8.0d); //강인한 반반의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591400, 5.0d); //위대한 반반의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591400, 1.0d); //위대한 반반의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2433591() { //블러디퀸의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591401, 15.0d); //기운찬 블러디퀸의 소울
        percentageInfo.put(2591402, 15.0d); //날렵한 블러디퀸의 소울
        percentageInfo.put(2591403, 15.0d); //총명한 블러디퀸의 소울
        percentageInfo.put(2591404, 15.0d); //놀라운 블러디퀸의 소울
        percentageInfo.put(2591405, 15.0d); //화려한 블러디퀸의 소울
        percentageInfo.put(2591406, 8.0d); //강력한 블러디퀸의 소울
        percentageInfo.put(2591407, 8.0d); //빛나는 블러디퀸의 소울
        percentageInfo.put(2591408, 8.0d); //강인한 블러디퀸의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591409, 5.0d); //위대한 블러디퀸의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591409, 1.0d); //위대한 블러디퀸의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2433592() { //벨룸의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591410, 15.0d); //기운찬 벨룸의 소울
        percentageInfo.put(2591411, 15.0d); //날렵한 벨룸의 소울
        percentageInfo.put(2591412, 15.0d); //총명한 벨룸의 소울
        percentageInfo.put(2591413, 15.0d); //놀라운 벨룸의 소울
        percentageInfo.put(2591414, 15.0d); //화려한 벨룸의 소울
        percentageInfo.put(2591415, 8.0d); //강력한 벨룸의 소울
        percentageInfo.put(2591416, 8.0d); //빛나는 벨룸의 소울
        percentageInfo.put(2591417, 8.0d); //강인한 벨룸의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591418, 5.0d); //위대한 벨룸의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591418, 1.0d); //위대한 벨룸의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2433593() { //스우의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591419, 15.0d); //기운찬 스우의 소울
        percentageInfo.put(2591420, 15.0d); //날렵한 스우의 소울
        percentageInfo.put(2591421, 15.0d); //총명한 스우의 소울
        percentageInfo.put(2591422, 15.0d); //놀라운 스우의 소울
        percentageInfo.put(2591423, 15.0d); //화려한 스우의 소울
        percentageInfo.put(2591424, 8.0d); //강력한 스우의 소울
        percentageInfo.put(2591425, 8.0d); //빛나는 스우의 소울
        percentageInfo.put(2591426, 8.0d); //강인한 스우의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591427, 5.0d); //위대한 스우의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591427, 1.0d); //위대한 스우의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2434035() { //돼지바 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591487, 15.0d); //기운찬 돼지바 소울
        percentageInfo.put(2591488, 15.0d); //날렵한 돼지바 소울
        percentageInfo.put(2591489, 15.0d); //총명한 돼지바 소울
        percentageInfo.put(2591490, 15.0d); //놀라운 돼지바 소울
        percentageInfo.put(2591491, 15.0d); //화려한 돼지바 소울
        percentageInfo.put(2591492, 8.0d); //강력한 돼지바 소울
        percentageInfo.put(2591493, 8.0d); //빛나는 돼지바 소울
        percentageInfo.put(2591494, 8.0d); //강인한 돼지바 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591495, 5.0d); //위대한 돼지바 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591495, 1.0d); //위대한 돼지바 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2434210() { //프리미엄 PC방 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591500, 12.5d); //기운찬 프리미엄PC방 소울
        percentageInfo.put(2591501, 12.5d); //날렵한 프리미엄PC방 소울
        percentageInfo.put(2591502, 12.5d); //총명한 프리미엄PC방 소울
        percentageInfo.put(2591503, 12.5d); //놀라운 프리미엄PC방 소울
        percentageInfo.put(2591504, 12.5d); //화려한 프리미엄PC방 소울
        percentageInfo.put(2591505, 12.5d); //강력한 프리미엄PC방 소울
        percentageInfo.put(2591506, 12.5d); //빛나는 프리미엄PC방 소울
        percentageInfo.put(2591507, 12.5d); //강인한 프리미엄PC방 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2434470() { //우르스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591509, 15.0d); //기운찬 우르스의 소울
        percentageInfo.put(2591510, 15.0d); //날렵한 우르스의 소울
        percentageInfo.put(2591511, 15.0d); //총명한 우르스의 소울
        percentageInfo.put(2591512, 15.0d); //놀라운 우르스의 소울
        percentageInfo.put(2591513, 15.0d); //화려한 우르스의 소울
        percentageInfo.put(2591514, 8.0d); //강력한 우르스의 소울
        percentageInfo.put(2591515, 8.0d); //빛나는 우르스의 소울
        percentageInfo.put(2591516, 8.0d); //강인한 우르스의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591517, 5.0d); //위대한 우르스의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591517, 1.0d); //위대한 우르스의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2435031() { //핑크몽의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591528, 14.0d); //기운찬 핑크몽의 소울
        percentageInfo.put(2591529, 14.0d); //날렵한 핑크몽의 소울
        percentageInfo.put(2591530, 14.0d); //총명한 핑크몽의 소울
        percentageInfo.put(2591531, 14.0d); //놀라운 핑크몽의 소울
        percentageInfo.put(2591532, 14.0d); //강인한 핑크몽의 소울
        percentageInfo.put(2591533, 14.0d); //풍부한 핑크몽의 소울
        percentageInfo.put(2591534, 14.0d); //화려한 핑크몽의 소울
        change_soul_piece(percentageInfo);
    }

    public void consume_2435369() { //데미안의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591564, 15.0d); //기운찬 데미안의 소울
        percentageInfo.put(2591565, 15.0d); //날렵한 데미안의 소울
        percentageInfo.put(2591566, 15.0d); //총명한 데미안의 소울
        percentageInfo.put(2591567, 15.0d); //놀라운 데미안의 소울
        percentageInfo.put(2591568, 15.0d); //화려한 데미안의 소울
        percentageInfo.put(2591569, 8.0d); //강력한 데미안의 소울
        percentageInfo.put(2591570, 8.0d); //빛나는 데미안의 소울
        percentageInfo.put(2591571, 8.0d); //강인한 데미안의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591572, 5.0d); //위대한 데미안의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591572, 1.0d); //위대한 데미안의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2436039() { //루시드의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591582, 15.0d); //기운찬 루시드의 소울
        percentageInfo.put(2591583, 15.0d); //날렵한 루시드의 소울
        percentageInfo.put(2591584, 15.0d); //총명한 루시드의 소울
        percentageInfo.put(2591585, 15.0d); //놀라운 루시드의 소울
        percentageInfo.put(2591586, 15.0d); //화려한 루시드의 소울
        percentageInfo.put(2591587, 8.0d); //강력한 루시드의 소울
        percentageInfo.put(2591588, 8.0d); //빛나는 루시드의 소울
        percentageInfo.put(2591589, 8.0d); //강인한 루시드의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591590, 5.0d); //위대한 루시드의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591590, 1.0d); //위대한 루시드의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2437478() { //파풀라투스의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591611, 15.0d); //기운찬 파풀라투스의 소울
        percentageInfo.put(2591612, 15.0d); //날렵한 파풀라투스의 소울
        percentageInfo.put(2591613, 15.0d); //총명한 파풀라투스의 소울
        percentageInfo.put(2591614, 15.0d); //놀라운 파풀라투스의 소울
        percentageInfo.put(2591615, 15.0d); //화려한 파풀라투스의 소울
        percentageInfo.put(2591616, 8.0d); //강력한 파풀라투스의 소울
        percentageInfo.put(2591617, 8.0d); //빛나는 파풀라투스의 소울
        percentageInfo.put(2591618, 8.0d); //강인한 파풀라투스의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591619, 5.0d); //위대한 파풀라투스의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591619, 1.0d); //위대한 파풀라투스의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2438396() { //윌의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591632, 15.0d); //기운찬 윌의 소울
        percentageInfo.put(2591633, 15.0d); //날렵한 윌의 소울
        percentageInfo.put(2591634, 15.0d); //총명한 윌의 소울
        percentageInfo.put(2591635, 15.0d); //놀라운 윌의 소울
        percentageInfo.put(2591636, 15.0d); //화려한 윌의 소울
        percentageInfo.put(2591637, 8.0d); //강력한 윌의 소울
        percentageInfo.put(2591638, 8.0d); //빛나는 윌의 소울
        percentageInfo.put(2591639, 8.0d); //강인한 윌의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591640, 5.0d); //위대한 윌의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591640, 1.0d); //위대한 윌의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2439567() { //진 힐라의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591651, 15.0d); //기운찬 진 힐라의 소울
        percentageInfo.put(2591652, 15.0d); //날렵한 진 힐라의 소울
        percentageInfo.put(2591653, 15.0d); //총명한 진 힐라의 소울
        percentageInfo.put(2591654, 15.0d); //놀라운 진 힐라의 소울
        percentageInfo.put(2591655, 15.0d); //화려한 진 힐라의 소울
        percentageInfo.put(2591656, 8.0d); //강력한 진 힐라의 소울
        percentageInfo.put(2591657, 8.0d); //빛나는 진 힐라의 소울
        percentageInfo.put(2591658, 8.0d); //강인한 진 힐라의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591659, 5.0d); //위대한 진 힐라의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591659, 1.0d); //위대한 진 힐라의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void consume_2439568() { //듄켈의 소울 조각
        LinkedHashMap<Integer, Double> percentageInfo = new LinkedHashMap<>();
        percentageInfo.put(2591668, 15.0d); //기운찬 듄켈의 소울
        percentageInfo.put(2591669, 15.0d); //날렵한 듄켈의 소울
        percentageInfo.put(2591670, 15.0d); //총명한 듄켈의 소울
        percentageInfo.put(2591671, 15.0d); //놀라운 듄켈의 소울
        percentageInfo.put(2591672, 15.0d); //화려한 듄켈의 소울
        percentageInfo.put(2591673, 8.0d); //강력한 듄켈의 소울
        percentageInfo.put(2591674, 8.0d); //빛나는 듄켈의 소울
        percentageInfo.put(2591675, 8.0d); //강인한 듄켈의 소울
        if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSoulGacha) {
            percentageInfo.put(2591676, 5.0d); //위대한 듄켈의 소울
            change_soul_piece_sunday(percentageInfo);
        } else {
            percentageInfo.put(2591676, 1.0d); //위대한 듄켈의 소울
            change_soul_piece(percentageInfo);
        }
    }

    public void change_soul_piece(HashMap<Integer, Double> pInfo) {
        StringBuilder sb = new StringBuilder();
        for (Integer pKey : pInfo.keySet()) {
            sb.append("\r\n#i").append(pKey).append(":# #t").append(pKey).append("#(").append(Math.round(pInfo.get(pKey))).append("%)");
        }
        if (getPlayer().getItemQuantity(itemID, false) < 10) {
            self.say("#b#t" + itemID + "##k \r\n#r10개#k를 모으면 아래의 아이템 중 1종으로 교환할 수 있다네.\r\n\r\n#b#e<각 소울의 등장 확률>#n#k#b" + sb.toString());
            return;
        }
        if (1 == self.askAccept("#b#t" + itemID + "##k 10개를 소비하여 소울 아이템으로 교환할텐가?\r\n\r\n#b#e<각 소울의 등장 확률>#n#k#b" + sb.toString())) {
            List<Integer> rKeys = new ArrayList<>(pInfo.keySet());
            Collections.shuffle(rKeys);
            double percent = 0.0;
            for (Integer rKey : rKeys) {
                percent += pInfo.get(rKey);
            }
            double random = percent - (Randomizer.nextDouble() * 100);
            double stack = 0.0d;
            int vou = 0;
            Iterator<Integer> ite = rKeys.iterator();
            while (ite.hasNext() && vou == 0) {
                int a = ite.next();
                Double p = pInfo.get(a);
                stack += p;
                if (stack >= random) {
                    vou = a;
                }
            }
            if (vou != 0) {
                if (target.exchange(itemID, -10, vou, 1) > 0) {
                    self.sayOk("오호~ #b#t" + vou + "##k이 나왔군.\r\n요긴하게 쓰게나. 크크크...");
                } else {
                    self.sayOk("소비창에 여유 공간이 부족하군. 확인하고 다시 시도해주게냐.");
                }
            } else {
                self.sayOk("잠시 오류가 발생했군 나중에 다시 시도해주게나...");
            }
        } else {
            self.sayOk("신중한 것도 나쁘진 않지.\r\n하지만 소울은 강력한 아이템이니 잘 생각해 보게...");
        }
    }

    public void change_soul_piece_sunday(HashMap<Integer, Double> pInfo) {
        if (getPlayer().getItemQuantity(itemID, false) < 10) {
            self.say("#b#t" + itemID + "##k \r\n#r10개#k를 모으면 아래의 아이템 중 1종으로 교환할 수 있다네.");
            return;
        }
        if (1 == self.askAccept("#r<선데이 메이플> 위대한 소울 등장확률 5배!#k\r\n#b#t" + itemID + "##k 10개를 소비하여 소울 아이템으로 교환할텐가?")) {
            List<Integer> rKeys = new ArrayList<>(pInfo.keySet());
            Collections.shuffle(rKeys);
            double percent = 0.0;
            for (Integer rKey : rKeys) {
                percent += pInfo.get(rKey);
            }
            double random = percent - (Randomizer.nextDouble() * 100);
            double stack = 0.0d;
            int vou = 0;
            Iterator<Integer> ite = rKeys.iterator();
            while (ite.hasNext() && vou == 0) {
                int a = ite.next();
                Double p = pInfo.get(a);
                stack += p;
                if (stack >= random) {
                    vou = a;
                }
            }
            if (vou != 0) {
                if (target.exchange(itemID, -10, vou, 1) > 0) {
                    self.sayOk("오호~ #b#t" + vou + "##k이 나왔군.\r\n요긴하게 쓰게나. 크크크...");
                } else {
                    self.sayOk("소비창에 여유 공간이 부족하군. 확인하고 다시 시도해주게냐.");
                }
            } else {
                self.sayOk("잠시 오류가 발생했군 나중에 다시 시도해주게나...");
            }
        } else {
            self.sayOk("신중한 것도 나쁘진 않지.\r\n하지만 소울은 강력한 아이템이니 잘 생각해 보게...");
        }
    }

}
