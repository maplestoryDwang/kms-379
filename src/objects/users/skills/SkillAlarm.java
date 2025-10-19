package objects.users.skills;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class SkillAlarm {
   private MapleCharacter player;
   private int[] skillList = new int[6];
   private boolean[] onOffCheck = new boolean[6];

   public SkillAlarm(MapleCharacter player) {
      this.player = player;
      this.load();
   }

   public void load() {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM skill_alarm WHERE `character_id` = ?");
         ps.setInt(1, this.player.getId());
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            String[] skill = rs.getString("alarm_list").split(",");

            for (int i = 0; i < this.skillList.length; i++) {
               this.skillList[i] = Integer.parseInt(skill[i]);
            }

            String[] onoff = rs.getString("alarm_onoff").split(",");

            for (int i = 0; i < this.skillList.length; i++) {
               this.onOffCheck[i] = Integer.parseInt(onoff[i]) != 0;
            }
         }

         rs.close();
         ps.close();
      } catch (SQLException var9) {
         var9.printStackTrace();
      }
   }

   public void encode(PacketEncoder mplew) {
      for (int i = 0; i < 6; i++) {
         mplew.writeInt(this.skillList[i]);
      }

      for (int i = 0; i < 6; i++) {
         mplew.write(this.onOffCheck[i]);
      }
   }

   public int[] getSkillList() {
      return this.skillList;
   }

   public boolean[] getOnOffCheck() {
      return this.onOffCheck;
   }
}
