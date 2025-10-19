package script.Util;

import database.DBConnection;
import network.center.Center;
import network.game.GameServer;
import objects.quest.QuestEx;
import objects.users.MapleCharacter;
import scripting.newscripting.ScriptEngineNPC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GMUtil extends ScriptEngineNPC {

    public void test1() {
        if (!getPlayer().isGM()) {
            return;
        }
        var type = self.askMenu("안녕하세요 #h 0#님, 유저 QuestInfo 수정용 엔피시입니다. 다음 중 수정할 타입을 선택해주세요.\r\n#L0#캐릭터 QuestInfo 수정\r\n#L1#어카운트 QuestInfo 수정");
        var name = self.askText("수정하실 유저의 이름을 입력해주세요. 어카운트의 경우 계정의 ID를 입력하셔도 됩니다. 해당 유저의 접속 여부는 상관이 없습니다.");
        int userID = findUserID(type, name);
        MapleCharacter user = null;
        if (userID >= 0) {
            for (GameServer srv : GameServer.getAllInstances()) {
                if ((user = srv.getPlayerStorage().getCharacterById(userID)) != null) {
                    break;
                }
            }
        } else {
            self.sayOk("해당 캐릭터 혹은 계정이 존재하지 않습니다. 다시 한 번 시도해 주세요.");
            return;
        }

        int questID = self.askNumber("변환하실 QuestEx의 ID를 입력해주세요.", 0, 0, Integer.MAX_VALUE);
        String questKey = self.askText("변환하실 QuestEx의 Key를 입력해주세요.");

        if (user != null) {
            //인게임 내 캐릭터가 존재한다.
            String questValue = user.getOneInfo(questID, questKey);
            String newValue = self.askText("해당 QuestEX는 현재 " + questValue + " 값입니다. 변환하실 값을 입력해주세요.");
            if (newValue != null) {
                user.updateOneInfo(questID, questKey, newValue);
                self.sayOk("성공적으로 변환하였습니다.");
            } else {
                self.sayOk("변환에 문제가 발생했습니다. 다시 시도해 주세요.");
            }
        } else {
            String newValue = self.askText("변환하실 QuestEX의 Value 값을 입력해주세요.");
            if (newValue != null) {
                if (updateUserQuestInfo(userID, type, questID, questKey, newValue)) {
                    self.sayOk("성공적으로 변환하였습니다.");
                } else {
                    self.sayOk("문제가 발생하여 DB를 Update하지 못했습니다.");
                }
            } else {
                self.sayOk("변환에 문제가 발생했습니다. 다시 시도해 주세요.");
            }
        }
    }

    public boolean updateUserQuestInfo(int userID, int type, int questID, String key, String value) {
        Map<Integer, QuestEx> questInfo = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String dbName = type == 1 ? "questinfo_account" : "questinfo";
        String idType = type == 1 ? "account_id" : "characterid";
        try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT `quest`, `customData`, `date` FROM " + dbName + " WHERE " +  idType + " = ?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            while (rs.next()) {
                questInfo.put(rs.getInt("quest"), new QuestEx(rs.getInt("quest"), rs.getString("customData"), rs.getString("date")));
            }

            if (key == null) {
                return false;
            }

            QuestEx ex = questInfo.get(questID);
            if (ex == null) {
                ps = con.prepareStatement("INSERT INTO " + dbName + " VALUES (DEFAULT, ?, ?, ?, ?)");
                ps.setInt(1, userID);
                ps.setInt(2, questID);
                ps.setString(3, value);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String time = sdf.format(Calendar.getInstance().getTime());
                ps.setString(4, time);
                ps.executeUpdate();
                ps.close();

                return true;
            }

            final String[] split = ex.getData().split(";");
            String oldData = null;
            for (String x : split) {
                final String[] split2 = x.split("="); //should be only 2
                if (split2.length == 2 && split2[0].equals(key)) {
                    oldData = split2[1];
                    break;
                }
            }

            rs.close();
            ps.close();

            if (oldData != null) {
                String newData = ex.getData().replaceAll(oldData, value);
                ex.setData(newData);

                ps = con.prepareStatement("UPDATE " + dbName + " SET customData = ? WHERE " + idType+ " = ? AND quest = ?");
                ps.setString(1, newData);
                ps.setInt(2, userID);
                ps.setInt(3, questID);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public int findUserID(int type, String name) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT * FROM characters WHERE `name` = ?");
            ps.setString(1, name);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

            ps.close();
            rs.close();

            if (type == 1) {
                ps = con.prepareStatement("SELECT * FROM accounts WHERE `name` = ?");
                ps.setString(1, name);

                rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return -1;
    }
}
