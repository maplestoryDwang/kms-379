package commands;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.NPCScriptManager;

public class RoyalCommand implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      String split = splitted[0];
      switch (split) {
         case "키벨류조작":
            if (splitted.length < 5) {
               c.getPlayer().dropMessage(6, "Syntax: !키벨류조작 캐릭명 type key value");
               return;
            }

            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
               victim.updateOneInfo(Integer.parseInt(splitted[2]), splitted[3], splitted[4]);
               c.getPlayer().dropMessage(6, "키벨류 수정완료");
            } else {
               c.getPlayer().dropMessage(6, "The victim does not exist.");
            }
            break;
         case "키벨류삭제":
            if (splitted.length < 4) {
               c.getPlayer().dropMessage(6, "!키벨류삭제 <닉네임> <번호> <키>");
               return;
            }

            MapleCharacter chrx = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrx == null) {
               c.getPlayer().dropMessage(6, "같은 채널에 없는듯?");
            } else {
               int t = Integer.parseInt(splitted[2]);
               String key = splitted[3];
               chrx.removeOneInfo(t, key);
               c.getPlayer().dropMessage(6, "Quest KeyValue Delete Complete");
            }
            break;
         case "캐릭터키벨류조작": {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
               c.getPlayer().dropMessage(6, "같은 채널에 없는듯?");
            } else {
               String key = splitted[2];
               String value = splitted[3];
               chr.setKeyValue(key, value);
               c.getPlayer().dropMessage(6, "Character KeyValue Set Complete");
            }
         }
            break;
         case "캐릭터키벨류삭제": {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(6, "!캐릭터키벨류삭제 <닉네임> <키>");
               return;
            }

            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
               c.getPlayer().dropMessage(6, "같은 채널에 없는듯?");
            } else {
               String key = splitted[2];
               chr.removeKeyValue(key);
               c.getPlayer().dropMessage(6, "Character KeyValue Delete Complete");
            }
         }
            break;
         case "계정키벨류조작": {
            if (splitted.length < 4) {
               c.getPlayer().dropMessage(6, "!계정키벨류조작 <닉네임> <키> <벨류>");
               return;
            }

            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
               c.getPlayer().dropMessage(6, "같은 채널에 없는듯?");
            } else {
               String key = splitted[2];
               String value = splitted[3];
               chr.getClient().setKeyValue(key, value);
               c.getPlayer().dropMessage(6, "Account KeyValue Set Complete");
            }
         }
            break;
         case "계정키벨류삭제": {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(6, "!계정키벨류삭제 <닉네임> <키>");
               return;
            }

            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
               c.getPlayer().dropMessage(6, "같은 채널에 없는듯?");
            } else {
               String key = splitted[2];
               chr.getClient().removeKeyValue(key);
               c.getPlayer().dropMessage(6, "Account KeyValue Delete Complete");
            }
         }
            break;
         case "우편함지급":
            if (splitted.length < 2) {
               c.getPlayer().dropMessage(5, "!우편함지급 <닉네임> <아이템코드> <개수>");
               return;
            }

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `id` FROM characters WHERE `name` = ?");
               ps.setString(1, splitted[1]);
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                  PreparedStatement pse = con
                        .prepareStatement("INSERT INTO `offline` (chrid, item, qua) VALUES (?, ?, ?)");
                  pse.setInt(1, rs.getInt("id"));
                  pse.setInt(2, Integer.parseInt(splitted[2]));
                  pse.setInt(3, Integer.parseInt(splitted[3]));
                  pse.executeUpdate();
                  pse.close();
                  c.getPlayer().dropMessage(5,
                        "[우편함] [" + splitted[2] + "] " + splitted[3] + " 개 를 " + splitted[1] + " 님의 우편함에 지급 하였습니다.");
               } else {
                  c.getPlayer().dropMessage(5, "존재하지 않는 플레이어입니다.");
               }

               rs.close();
               ps.close();
            } catch (SQLException var15) {
               var15.printStackTrace();
            }
            break;
         case "우편함비우기":
            if (splitted.length < 2) {
               c.getPlayer().dropMessage(5, "!우편함비우기 <닉네임>");
               return;
            }

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `id` FROM characters WHERE `name` = ?");
               ps.setString(1, splitted[1]);
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                  PreparedStatement pse = con.prepareStatement("DELETE FROM `offline` WHERE chrid = ?");
                  pse.setInt(1, rs.getInt("id"));
                  pse.execute();
                  pse.close();
                  c.getPlayer().dropMessage(5, "[우편함] " + splitted[1] + " 님의 우편함을 비웠습니다.");
               } else {
                  c.getPlayer().dropMessage(5, "존재하지 않는 플레이어입니다.");
               }

               rs.close();
               ps.close();
            } catch (SQLException var13) {
               var13.printStackTrace();
            }
            break;
         case "택배":
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            NPCScriptManager.getInstance().start(c, 9900000);
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[] {
            new CommandDefinition("키벨류조작", "", "", 6),
            new CommandDefinition("키벨류삭제", "", "", 6),
            new CommandDefinition("캐릭터키벨류조작", "", "", 6),
            new CommandDefinition("캐릭터키벨류삭제", "", "", 6),
            new CommandDefinition("계정키벨류조작", "", "", 6),
            new CommandDefinition("계정키벨류삭제", "", "", 6),
            new CommandDefinition("우편함지급", "", "", 6),
            new CommandDefinition("우편함비우기", "", "", 6),
            new CommandDefinition("택배", "", "", 6)
      };
   }
}
