package objects.users.skills;

import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public final class VCore {
   private int coreid;
   private int id;
   private int charid;
   private int level;
   private int exp;
   private int state;
   private int skill1;
   private int skill2;
   private int skill3;
   private int position;
   private int index;
   private long crcid;
   private long availableTime;
   private boolean locked;
   private VSpecialCoreOption specialCoreOption;

   public VCore(
      long crcid,
      int coreid,
      int charid,
      int level,
      int exp,
      int state,
      int skill1,
      int skill2,
      int skill3,
      VSpecialCoreOption specialCoreOption,
      long availableTime,
      int position,
      boolean locked
   ) {
      this.setCrcid(crcid);
      this.setCoreId(coreid);
      this.setCharid(charid);
      this.setLevel(level);
      this.setExp(exp);
      this.setState(state);
      this.setSkill1(skill1);
      this.setSkill2(skill2);
      this.setSkill3(skill3);
      this.setSpecialCoreOption(specialCoreOption);
      this.setAvailableTime(availableTime);
      this.setPosition(position);
      this.setLocked(locked);
   }

   public long getCrcid() {
      return this.crcid;
   }

   public void setCrcid(long crcid) {
      this.crcid = crcid;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getCoreId() {
      return this.coreid;
   }

   public void setCoreId(int id) {
      this.coreid = id;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getExp() {
      return this.exp;
   }

   public void setExp(int exp) {
      this.exp = exp;
   }

   public int getState() {
      return this.state;
   }

   public void setState(int state) {
      this.state = state;
   }

   public int getSkill1() {
      return this.skill1;
   }

   public void setSkill1(int skill1) {
      this.skill1 = skill1;
   }

   public int getSkill2() {
      return this.skill2;
   }

   public void setSkill2(int skill2) {
      this.skill2 = skill2;
   }

   public int getSkill3() {
      return this.skill3;
   }

   public void setSkill3(int skill3) {
      this.skill3 = skill3;
   }

   public int getCharid() {
      return this.charid;
   }

   public void setCharid(int charid) {
      this.charid = charid;
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public static void LoadVCore(MapleCharacter chr) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement(
            "SELECT crc_id, core_id, level, exp, state, skill_1, skill_2, skill_3, available_time, position, locked FROM vcores WHERE player_id = ?", 1
         );
         ps.setInt(1, chr.getId());
         rs = ps.executeQuery();

         while (rs.next()) {
            int coreId = rs.getInt("core_id");
            VCoreData.VCoreInfo info = VCoreData.getCoreInfo(coreId);
            if (info != null) {
               VSpecialCoreOption specialCoreOption = VCoreData.getSpecialCoreOption(coreId);
               int skill1 = replaceRemasterSkill(rs.getInt("skill_1"));
               int skill2 = replaceRemasterSkill(rs.getInt("skill_2"));
               int skill3 = replaceRemasterSkill(rs.getInt("skill_3"));
               VCore core = new VCore(
                  rs.getLong("crc_id"),
                  coreId,
                  chr.getId(),
                  rs.getInt("level"),
                  rs.getInt("exp"),
                  rs.getInt("state"),
                  skill1,
                  skill2,
                  skill3,
                  specialCoreOption,
                  rs.getLong("available_time"),
                  rs.getInt("position"),
                  rs.getInt("locked") > 0
               );
               if (specialCoreOption != null && core.getAvailableTime() == -1L) {
                  core.setAvailableTime(System.currentTimeMillis() + 1209600000L);
               }

               chr.addVCoreSkillsNoLock(core);
               if (core.getState() == 2 && specialCoreOption != null) {
                  chr.setEquippedSpecialCore(new Pair<>(core.getCoreId(), specialCoreOption.getSkillId()));
               }
            }
         }
      } catch (Exception var24) {
         System.out.println("VCore Err");
         var24.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }

            if (rs != null) {
               rs.close();
            }
         } catch (SQLException var21) {
         }
      }
   }

   public static int replaceRemasterSkill(int skillID) {
      switch (skillID) {
         case 400011005:
            return 400011142;
         default:
            return skillID;
      }
   }

   public static void LoadVMatrixSlots(MapleCharacter chr) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT `index`, slot_enforcement, released FROM vmatrixslot WHERE player_id = ?", 1);
         ps.setInt(1, chr.getId());
         rs = ps.executeQuery();

         for (int i = 0; i < 25; i++) {
            VMatrixSlot matrixSlot = new VMatrixSlot();
            matrixSlot.setIndex(i);
            matrixSlot.setReleased(0);
            matrixSlot.setSlotEnforcement(0);
            int equippedCore = -1;

            for (VCore core : chr.getVCoreSkillsNoLock()) {
               if (core.getState() == 2 && core.getPosition() == i) {
                  equippedCore = core.getCoreId();
                  break;
               }
            }

            matrixSlot.setEquippedCore(equippedCore);
            chr.addVMatrixSlot(matrixSlot);
         }

         boolean find = false;

         while (rs.next()) {
            if (!find) {
               find = true;
            }

            int equippedCore = -1;
            int index = rs.getInt("index");
            int slotEnforcement = rs.getInt("slot_enforcement");
            int released = rs.getInt("released");

            for (VCore corex : chr.getVCoreSkillsNoLock()) {
               if (corex.getState() == 2 && corex.getPosition() == index) {
                  equippedCore = corex.getCoreId();
                  break;
               }
            }

            VMatrixSlot matrixSlot = chr.getVMatrixSlot(index);
            matrixSlot.setReleased(released);
            matrixSlot.setSlotEnforcement(slotEnforcement);
            matrixSlot.setEquippedCore(equippedCore);
         }

         if (!find) {
            chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.V_MATRIX_SLOTS.getFlag());
         }
      } catch (Exception var24) {
         System.out.println("VCore 2 Err");
         var24.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }

            if (rs != null) {
               rs.close();
            }
         } catch (SQLException var21) {
         }
      }
   }

   public VSpecialCoreOption getSpecialCoreOption() {
      return this.specialCoreOption;
   }

   public void setSpecialCoreOption(VSpecialCoreOption specialCoreOption) {
      this.specialCoreOption = specialCoreOption;
   }

   public long getAvailableTime() {
      return this.availableTime;
   }

   public void setAvailableTime(long availableTime) {
      this.availableTime = availableTime;
   }
}
