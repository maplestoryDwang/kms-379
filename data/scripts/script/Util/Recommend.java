package script.Util;

import database.DBConnection;
import network.center.Center;
import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.utils.Pair;
import scripting.newscripting.ScriptEngineNPC;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Recommend extends ScriptEngineNPC {

    //담에 추천인 구조 꼭 바꾸자.. 제발..

    Pair<Integer, Integer> etcReward = new Pair<>(4001715, 10);

    List<Pair<Integer, Integer>> selfReward = List.of(
            new Pair<>(4001715, 10),
            new Pair<>(4310266, 300)
    );

    Map<Integer, List<Pair<Integer, Integer>>> reward = Map.of(
            1000, List.of(new Pair<>(5068301, 5)),
            5000, List.of(new Pair<>(5068301, 20)),
            7000, List.of(new Pair<>(5068301, 40))
    );

    public int getAccIdFromDB(String name) {
        int ret = -1;
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT * FROM characters WHERE name LIKE '" + name + "%'").executeQuery()) {
                if (con.next()) {
                    ret = con.getInt("accountid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void join_recom(int name, String name2, String recom) {
        try (var con = DBConnection.getConnection()) {
            try (var insert = con.prepareStatement("INSERT INTO recom_log(name, recom, state, date) VALUES(?,?,?,now())")) {
                insert.setString(1, name + "%" + name2);
                insert.setString(2, recom);
                insert.setString(3, "0");
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existChar(String name) {
        boolean overlab = true;
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT * FROM characters WHERE name LIKE '" + name + "%'").executeQuery()) {
                if (!con.next())
                    overlab = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overlab;
    }

    public boolean overlab_recom(int name, String name2) {
        boolean overlab = true;
        try (Connection c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT * FROM recom_log WHERE `name` LIKE '" + name + "%'").executeQuery()) {
                overlab = con.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return overlab;
    }

    public String recom_log() {
        var txt = new StringBuilder();
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT id, recom, COUNT(recom) as player FROM recom_log GROUP BY recom HAVING COUNT(recom) > 0 order by COUNT(recom) desc").executeQuery()) {
                var rank = 0;
                while (con.next()) {
                    txt.append("#L").append(con.getInt("id")).append("#")
                            .append(rank == 0 ? "#r "//#fUI/UIWindow2.img/ProductionSkill/productPage/meister#
                                    : rank == 1 ? "#b "//#fUI/UIWindow2.img/ProductionSkill/productPage/craftman#
                                    : "#k ")//#fUI/UIWindow2.img/ProductionSkill/productPage/hidden#

                            .append("추천인 코드 #k: ").append(con.getString("recom")).append(" | ")
                            .append("추천 수 #k: #e").append(con.getString("player")).append("#n\r\n");
                    rank++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return txt.toString();
    }

    public int recoms_count(String name) {
        var a = 0;
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT * FROM recom_log WHERE recom LIKE '" + name + "'").executeQuery()) {
                while (con.next()) {
                    if (con.getString("recom").equals(name)) {
                        a++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return a;
    }

    public int recom_num(String name) {
        int recoms_num = 0;
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT COUNT(*) AS player FROM recom_log WHERE recom = '" + name + "' and state = 0").executeQuery()) {
                if (con.next()) {
                    recoms_num = con.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recoms_num;
    }

    public String recom_person(String name) {
        var txt = new StringBuilder();
        try (var c = DBConnection.getConnection()) {
            try (var con = c.prepareStatement("SELECT * FROM recom_log WHERE recom = '" + name + "' and state = 0").executeQuery()) {
                while (con.next()) {
                    var con_name = con.getString("name").split("%");
                    txt.append("#b[").append(con_name[1]).append("] ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return txt.toString();
    }

    public String recom_list(int id) {
        var txt = new StringBuilder();
        try (Connection c = DBConnection.getConnection()) {
            try (var idcon = c.prepareStatement("SELECT * FROM recom_log WHERE id = '" + id + "'").executeQuery()) {
                if (idcon.next()) {
                    String recom_per = idcon.getString("recom");

                    var con = c.prepareStatement("SELECT * FROM recom_log WHERE recom = '" + recom_per + "'").executeQuery();
                    txt.append(recom_per).append("님을 추천하신 플레이어들 입니다.\r\n\r\n");
                    while (con.next()) {
                        var con_name = con.getString("name").split("%");
                        txt.append("닉네임 : #e").append(con_name[1]).append("#n | ")
                                .append("날짜 : ").append(con.getDate("date") + " " + con.getTimestamp("date")).append("\r\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return txt.toString();
    }

    public void recommend() {
        initNPC(MapleLifeFactory.getNPC(9062454));
        StringBuilder str = new StringBuilder("#fs11##b #h 0##fc0xFF000000#님, #fc0xFF990033#[강림]#fc0xFF000000#에 오신 것을 환영합니다.\r\n\r\n")
                .append("#r(추천은 1인 1계정 레벨 200이상 가능합니다.)#k\r\n")
                .append("#fUI/UIWindow.img/UtilDlgEx/list1#\r\n")
                .append("#L0##b추천인#fc0xFF000000# 등록하기\r\n")
                .append("#L1##b추천인#fc0xFF000000# 랭킹보기#l\r\n\r\n")
                .append("#fUI/UIWindow.img/UtilDlgEx/list0#\r\n")
                .append("#L2##b추천인#fc0xFF000000# 확인하기\r\n");

        int menu = self.askMenu(str.toString());
        switch (menu) {
            case 0: {
                if (!overlab_recom(getPlayer().getClient().getAccID(), getPlayer().getName())) {
                    if (getPlayer().getLevel() >= 200) {
                        str = new StringBuilder("#b#fs11# #h 0##fc0xFF000000#님, 당신을 #fc0xFF990033#[강림월드]#fc0xFF000000#로 이끈 이의 #b닉네임#fc0xFF000000#을 말씀해주세요.\r\n");
                        str.append("하지만 #r한 번 등록하면 되돌릴 수 없으니#fc0xFF000000# 신중하게 등록하셔야 해요.");
                        String text = self.askText(str.toString());

                        if (!existChar(text)) {
                            self.sayOk("없는 유저입니다.");
                            return;
                        }
                        if (text.equals("") || text.equals(getPlayer().getName()) || getAccIdFromDB(text) == getAccIdFromDB(getPlayer().getName())) {
                            self.sayOk(text.equals("") ? "입력을 잘못 하셨습니다." : "자기 자신을 등록 할 수는 없습니다.");
                        } else {
                            join_recom(getClient().getAccID(), getPlayer().getName(), text);
                            getPlayer().gainItem(etcReward.left, etcReward.right);

                            str = new StringBuilder("#fs11#이건 #b#h 0##k님에게 드리는 저의 작은 선물입니다. 앞으로의 여행에 큰 도움이 될 거예요.#b\r\n");

                            for (Pair<Integer, Integer> reward : selfReward) {
                                getPlayer().gainItem(reward.left, reward.right);
                                str.append(String.format("#i%d##z%d# %d개\r\n", reward.left, reward.left, reward.right));
                            }

                            self.sayOk(str.toString());
                            Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(11, getClient().getChannel(), "[추천인] " + getPlayer().getName() + " 님이 " + text + " 님을 추천인으로 등록하셨습니다."));
                        }
                    } else {
                        self.sayOk("#fs11##r200레벨 미만#k은 추천인을 등록할 수 없어요.");
                    }
                } else {
                    self.sayOk("#fs11##fc0xFF000000#추천인은 한번만 작성가능합니다.");
                }
                break;
            }
            case 1: {
                str = new StringBuilder("#fs11##fc0xFF000000#이곳은 많은추천을 받은분들의 목록이에요.\r\n");
                str.append("#b#h 0##fc0xFF000000#님께서도 조금만 노력한다면 이곳에 오르실 수 있어요.\r\n");
                str.append(recom_log());
                self.sayOk(recom_list(self.askMenu(str.toString())));
                break;
            }
            case 2: {
                int recoms_num = recom_num(getPlayer().getName());
                if (recoms_num == 0) {
                    str = new StringBuilder("#fs11##fc0xFF000000#아직 #b#h 0##fc0xFF000000#님을 추천하신 분이 없네요.\r\n");
                    str.append("열심히 #fc0xFF990033#[강림]#fc0xFF000000#를 알린다면, 보상이 따를 것 입니다.");
                    self.sayOk(str.toString());
                } else {
                    self.sayOk("#b #h 0##fc0xFF000000#님을 추천한 분들 입니다. " + recoms_num + "명 " + recom_person(getPlayer().getName()) + "#fc0xFF000000#의 추천을 받으셨어요.");
                    getPlayer().gainHPoint(100000 * recoms_num);
                    getPlayer().dropMessage(1, 100000 * recoms_num + " 홍보 포인트를 지급 받았습니다.");
                    try (var c = DBConnection.getConnection()) {
                        var ps = c.prepareStatement("UPDATE recom_log SET state = 1 WHERE recom = '" + getPlayer().getName() + "'");
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case 3: {
                int a = recoms_count(getPlayer().getName());

                StringBuilder msg = new StringBuilder("#fs11#받으실 보상을 선택해주세요.\r\n현재 #b#h ##k님의 추천 수는 개 입니다.#fs11#\r\n");

                int i = 0;
                for (Integer entry : reward.keySet()) {
                    if (entry <= a && getClient().getKeyValue("recom_" + entry) == null) {
                        msg.append("#L").append(entry).append("##b").append(entry).append("명 보상 (수령 가능)\r\n");
                    } else {
                        msg.append("#L").append(entry).append("##r").append(entry).append("명 보상 (수령 불가)\r\n");
                    }
                    i++;
                }

                int select = self.askMenu(msg.toString());

                msg = new StringBuilder("다음은 누적 " + reward.get(select) + "명 보상입니다.#b#fs11#\r\n");

                for (Map.Entry<Integer, List<Pair<Integer, Integer>>> entry : reward.entrySet()) {
                    var itemList = reward.get(select);
                    for (Pair<Integer, Integer> item : itemList) {
                        msg.append("#i").append(item.left).append("##z").append(item.left).append("# ").append(item.right).append("개\r\n");
                    }
                }

                if (select <= a && getClient().getKeyValue("recom_" + select) == null) {
                    getClient().setKeyValue("recom_" + select, "1");
                    for (Map.Entry<Integer, List<Pair<Integer, Integer>>> entry : reward.entrySet()) {
                        var itemList = reward.get(select);
                        for (Pair<Integer, Integer> item : itemList) {
                            getPlayer().gainItem(item.left, item.right);
                        }
                    }
                }
                self.sayOk(msg.toString());
                break;
            }
        }
    }
}
