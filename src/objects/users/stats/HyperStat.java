package objects.users.stats;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;

public class HyperStat {
   public MapleCharacter player;
   public int currentIndex;
   public List<HyperStat.HyperStatData> statDataList;

   public HyperStat(MapleCharacter chr, int index) {
      this.player = chr;
      this.init(index);
   }

   public void init(int index) {
      this.currentIndex = index;
      this.statDataList = new ArrayList<HyperStat.HyperStatData>() {
         {
            for (int i = 0; i < 3; i++) {
               this.add(new HyperStat.HyperStatData(HyperStat.this.player, i));
            }
         }
      };
   }

   public HyperStat.HyperStatData getStat() {
      return this.statDataList.get(this.currentIndex);
   }

   public void encode(PacketEncoder e) {
      e.write(this.currentIndex);
      this.statDataList.forEach(statData -> statData.encode(e));
   }

   public static int getMaxHyperStatPoint(MapleCharacter player) {
      int hsp = 0;

      for (int next = 140; next <= player.getLevel(); next++) {
         hsp += next / 10 - 11;
      }

      return hsp;
   }

   public static int getMaxHyperStatLevel(int skillID) {
      return skillID == 80000406 ? 10 : 15;
   }

   public static int getNeedTotalHyperStatPoint(int level) {
      switch (level) {
         case 1:
            return 1;
         case 2:
            return 3;
         case 3:
            return 7;
         case 4:
            return 15;
         case 5:
            return 25;
         case 6:
            return 40;
         case 7:
            return 60;
         case 8:
            return 85;
         case 9:
            return 115;
         case 10:
            return 150;
         case 11:
            return 200;
         case 12:
            return 265;
         case 13:
            return 345;
         case 14:
            return 440;
         case 15:
            return 550;
         default:
            return 0;
      }
   }

   public static int getNeedHyperStatPoint(int level) {
      switch (level) {
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            return 4;
         case 4:
            return 8;
         case 5:
            return 10;
         case 6:
            return 15;
         case 7:
            return 20;
         case 8:
            return 25;
         case 9:
            return 30;
         case 10:
            return 35;
         case 11:
            return 50;
         case 12:
            return 65;
         case 13:
            return 80;
         case 14:
            return 95;
         case 15:
            return 110;
         default:
            return 0;
      }
   }

   public static class HyperStatData {
      public MapleCharacter player;
      public int index;
      public List<HyperStat.HyperStatInfo> skillData;

      public HyperStatData(MapleCharacter player, int index) {
         this.player = player;
         this.index = index;
         this.skillData = new ArrayList<>();

         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hyper_stats WHERE player_id = ? AND `index` = ?");
         ) {
            ps.setInt(1, player.getId());
            ps.setInt(2, index);

            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                  HyperStat.HyperStatInfo info = new HyperStat.HyperStatInfo(rs.getInt("skill_id"), rs.getInt("skill_level"));
                  this.skillData.add(info);
               }
            }
         } catch (SQLException var14) {
            var14.printStackTrace();
         }

         for (int skillID = 80000400; skillID <= 80000422; skillID++) {
            if (SkillFactory.getSkill(skillID) != null && this.getSkillLevel(skillID) <= 0) {
               this.skillData.add(new HyperStat.HyperStatInfo(skillID, 0));
            }
         }
      }

      public void encode(PacketEncoder e) {
         e.writeInt(this.skillData.size());

         for (HyperStat.HyperStatInfo skill : this.skillData) {
            e.writeInt(this.skillData.indexOf(skill));
            e.writeInt(skill.skillID);
            e.writeInt(skill.skillLV);
         }
      }

      public int getSkillLevel(int skillID) {
         HyperStat.HyperStatInfo sData = this.skillData.stream().filter(data -> data.skillID == skillID).findFirst().orElse(null);
         return sData != null ? sData.skillLV : 0;
      }

      public void setSkillLevel(int skillID, int skillLV) {
         for (HyperStat.HyperStatInfo info : this.skillData) {
            if (info.skillID == skillID) {
               info.skillLV = skillLV;
            }
         }
      }

      public int getRemainStatPoint() {
         int totalPoint = HyperStat.getMaxHyperStatPoint(this.player);

         for (int skillID = 80000400; skillID <= 80000422; skillID++) {
            int skillLevel = this.getSkillLevel(skillID);
            if (skillLevel > 0) {
               totalPoint -= HyperStat.getNeedTotalHyperStatPoint(skillLevel);
            }
         }

         return totalPoint;
      }

      public void resetHyperStats() {
         for (int skillID = 80000400; skillID <= 80000422; skillID++) {
            int skillLevel = this.getSkillLevel(skillID);
            Skill skill = SkillFactory.getSkill(skillID);
            if (skill != null && skillLevel > 0) {
               this.setSkillLevel(skillID, 0);
            }
         }
      }
   }

   public static class HyperStatInfo {
      public int skillID;
      public int skillLV;

      public HyperStatInfo(int skillID, int skillLV) {
         this.skillID = skillID;
         this.skillLV = skillLV;
      }
   }
}
