package objects.users.stats;

import constants.HexaMatrixConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.utils.Pair;

public class HexaCore {
   private int playerid;
   private int maxStatIndex = 0;
   private boolean skillCoreChanged = false;
   private Map<Integer, Integer> skillDataMap = Collections.synchronizedMap(new HashMap<>());
   private Map<Integer, Integer> skillLv = Collections.synchronizedMap(new HashMap<>());
   private Map<Integer, Boolean> coreDisabled = Collections.synchronizedMap(new HashMap<>());
   private Map<Integer, Boolean> skillDisabled = Collections.synchronizedMap(new HashMap<>());
   private Map<Integer, HexaCore.HexaStatData> statDataMap = Collections.synchronizedMap(new HashMap<>());
   private HexaCore.HexaStatData savedStatData = null;

   public HexaCore(int playerid) {
      this.playerid = playerid;
      this.init();
   }

   public void init() {
      if (this.skillDataMap.isEmpty()) {
         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `hexa_cores` WHERE `player_id` = ?");
         ) {
            ps.setInt(1, this.playerid);

            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                  int coreid = rs.getInt("coreid");
                  int level = rs.getInt("level");
                  this.skillDataMap.put(coreid, level);
               }
            }
         } catch (SQLException var12) {
            var12.printStackTrace();
         }
      }

      if (this.statDataMap.isEmpty()) {
         for (int i = 0; i <= this.maxStatIndex; i++) {
            this.statDataMap.put(i, new HexaCore.HexaStatData(this.playerid, i));
         }
      }

      if (this.savedStatData == null) {
         HexaCore.HexaStatData check = new HexaCore.HexaStatData(this.playerid, -1);
         if (!check.getStats().isEmpty()) {
            this.savedStatData = check;
         }
      }
   }

   public void setSkillCoreLevel(MapleCharacter chr, int coreId, int level) {
      this.skillDataMap.put(coreId, level);
      this.checkDisabledSkillCore(chr);
      List<Integer> coreSkillList = HexaMatrixConstants.linkedCoreSkill.get(coreId);
      if (coreSkillList != null) {
         boolean isCoreDisabled = this.coreDisabled.getOrDefault(coreId, false);

         for (int skillId : coreSkillList) {
            Skill skill = SkillFactory.getSkill(skillId);
            byte level_ = (byte)level;
            if (skill != null) {
               byte masterlevel = (byte)skill.getMaxLevel();
               if (level_ > skill.getMaxLevel()) {
                  level_ = (byte)skill.getMaxLevel();
               }

               this.skillLv.put(skillId, Integer.valueOf(level_));
               if (isCoreDisabled) {
                  if (this.skillDisabled.getOrDefault(skillId, false)) {
                     this.skillDisabled.put(skillId, true);
                  }

                  level_ = 0;
               }

               chr.changeSkillLevel(skill, level_, masterlevel);
            }
         }
      }

      this.skillCoreChanged = true;
   }

   public int getSkillCoreLevel(int coreId) {
      return this.skillDataMap.getOrDefault(coreId, 0);
   }

   public void removeSkillCore(int coreId) {
      this.skillDataMap.remove(coreId);
      this.skillCoreChanged = true;
   }

   public Map<Integer, Integer> getSkillCores() {
      return new HashMap<>(this.skillDataMap);
   }

   public void saveCheck() {
      try {
         for (int i = 0; i <= this.maxStatIndex; i++) {
            if (this.statDataMap.get(i) != null && this.statDataMap.get(i).changed) {
               this.statDataMap.get(i).saveHexaStat();
               this.statDataMap.get(i).setChanged(false);
            }
         }

         if (this.savedStatData != null && this.savedStatData.changed) {
            this.savedStatData.saveHexaStat();
            this.savedStatData.setChanged(false);
         }

         if (this.skillCoreChanged) {
            try (Connection con = DBConnection.getConnection()) {
               try (PreparedStatement ps = con.prepareStatement("DELETE FROM `hexa_cores` WHERE `player_id` = ?")) {
                  ps.setInt(1, this.playerid);
                  ps.executeUpdate();
               }

               try (PreparedStatement ps = con.prepareStatement("INSERT INTO `hexa_cores` (`player_id`, `coreid`, `level`) VALUES(?, ?, ?)")) {
                  ps.setInt(1, this.playerid);

                  for (Entry<Integer, Integer> entry : this.skillDataMap.entrySet()) {
                     ps.setInt(2, entry.getKey());
                     ps.setInt(3, entry.getValue());
                     ps.executeUpdate();
                  }
               }

               this.skillCoreChanged = false;
            } catch (SQLException var11) {
               var11.printStackTrace();
            }
         }
      } catch (Exception var12) {
         System.out.println("헥사스텟 저장 오류");
         var12.printStackTrace();
      }
   }

   public HexaCore.HexaStatData getSavedStatData() {
      return this.savedStatData;
   }

   public void setSavedStatData(HexaCore.HexaStatData statData) {
      this.savedStatData = statData;
   }

   public HexaCore.HexaStatData getStat(int index) {
      return this.statDataMap.get(index);
   }

   public void setStat(int index, HexaCore.HexaStatData data) {
      this.statDataMap.put(index, data);
   }

   public int getStatSize() {
      return this.statDataMap.size();
   }

   public void updateSkills(MapleCharacter chr) {
      if (this.getStatSize() > 0) {
         Skill skill = SkillFactory.getSkill(500071000);
         byte level = 1;
         byte masterlevel = (byte)skill.getMaxLevel();
         if (level > skill.getMaxLevel()) {
            level = (byte)skill.getMaxLevel();
         }

         chr.changeSkillLevel(skill, level, masterlevel);
      }

      this.checkDisabledSkillCore(chr);
      Map<Integer, Integer> skillCores = this.getSkillCores();
      List<Integer> toRemoveList = new ArrayList<>();
      if (!skillCores.isEmpty()) {
         for (Entry<Integer, Integer> entry : skillCores.entrySet()) {
            int coreId = entry.getKey();
            int coreType = coreId / 10000000;
            int job = chr.getJob();
            boolean disabledCore = this.coreDisabled.getOrDefault(coreId, false);
            if (!disabledCore) {
               this.skillDisabled.getOrDefault(coreId, false);
            }

            if (coreType == 1 && !HexaMatrixConstants.sixthJobSkillCore.get(job).contains(coreId)) {
               toRemoveList.add(coreId);
            } else if (coreType == 2 && !HexaMatrixConstants.sixthJobMasteryCore.get(job).contains(coreId)) {
               toRemoveList.add(coreId);
            } else if (coreType == 3 && !HexaMatrixConstants.sixthJobEnforceCore.get(job).contains(coreId)) {
               toRemoveList.add(coreId);
            } else if (coreType == 4 && !HexaMatrixConstants.sixthJobCommonCore.get(job).contains(coreId)) {
               toRemoveList.add(coreId);
            } else {
               List<Integer> coreSkillList = HexaMatrixConstants.linkedCoreSkill.get(coreId);
               if (coreSkillList != null) {
                  for (int skillId : coreSkillList) {
                     Skill skill = SkillFactory.getSkill(skillId);
                     if (skill != null) {
                        int level = entry.getValue();
                        int masterlevel = skill.getMaxLevel();
                        if (level > skill.getMaxLevel()) {
                           level = skill.getMaxLevel();
                        }

                        this.skillLv.put(skillId, level);
                        if (skill.hasRequiredSkill()) {
                           for (Pair<String, Integer> check : skill.getRequiredSkills()) {
                              int reqSkillId = Integer.parseInt(check.getLeft());
                              int reqSkillLv = check.getRight();
                              if (chr.getTotalSkillLevel(reqSkillId) < reqSkillLv) {
                                 level = 0;
                                 break;
                              }
                           }
                        }

                        if (disabledCore) {
                           if (this.skillDisabled.getOrDefault(skillId, false)) {
                              this.skillDisabled.put(skillId, true);
                           }

                           level = 0;
                        }

                        chr.changeSkillLevel(skill, level, masterlevel);
                     }
                  }
               }
            }
         }
      }

      if (!toRemoveList.isEmpty()) {
         for (int coreIdx : toRemoveList) {
            this.removeSkillCore(coreIdx);
         }
      }
   }

   int linked6thSkill(int skillId) {
      switch (skillId) {
         case 500061000:
         case 500061001:
            return 500004004;
         case 500061002:
         case 500061003:
            return 500004020;
         case 500061004:
         case 500061005:
            return 500004073;
         case 500061006:
            return 500004096;
         case 500061007:
            return 500004116;
         case 500061008:
         case 500061009:
            return 500004132;
         case 500061010:
         case 500061011:
            return 500004135;
         case 500061012:
            return 500004166;
         case 500061013:
            return 500004173;
         case 500061014:
         case 500061015:
            return 500004180;
         case 500061016:
         case 500061017:
            return 500004027;
         case 500061018:
         case 500061019:
         case 500061020:
         case 500061021:
         case 500061022:
         case 500061023:
         case 500061024:
            return 500004034;
         case 500061025:
         case 500061026:
         case 500061027:
         case 500061028:
            return 500004040;
         case 500061029:
         case 500061030:
         case 500061031:
         case 500061032:
            return 500004059;
         case 500061033:
         case 500061034:
            return 500004070;
         case 500061035:
            return 500004074;
         case 500061036:
         case 500061037:
         case 500061038:
            return 500004076;
         case 500061039:
         case 500061040:
            return 500004080;
         case 500061041:
         case 500061042:
         case 500061043:
         case 500061044:
         case 500061045:
            return 500004084;
         case 500061046:
         case 500061047:
         case 500061048:
         case 500061049:
            return 500004088;
         case 500061050:
         case 500061051:
         case 500061052:
         case 500061053:
            return 500004089;
         case 500061054:
         case 500061055:
         case 500061056:
         case 500061057:
         case 500061058:
            return 500004111;
         case 500061059:
         case 500061060:
            return 500004125;
         case 500061061:
         default:
            return 0;
         case 500061062:
         case 500061063:
         case 500061064:
            return 500004152;
         case 500061065:
            return 500004162;
         case 500061066:
         case 500061067:
         case 500061068:
            return 500004167;
      }
   }

   public int getSkillLevel(int skillId) {
      int linked = this.linked6thSkill(skillId);
      if (this.skillDisabled.getOrDefault(linked > 0 ? linked : skillId, false)) {
         return 0;
      } else {
         return linked > 0 ? this.skillLv.getOrDefault(linked, 0) : this.skillLv.getOrDefault(skillId, 0);
      }
   }

   public boolean isDisabledSkillCore(int coreId) {
      return this.coreDisabled.getOrDefault(coreId, false);
   }

   public void checkDisabledSkillCore(MapleCharacter chr) {
      this.coreDisabled.clear();
      this.skillDisabled.clear();
      if (!chr.getClient().isGm()) {
         Map<Integer, Integer> skillCores = this.getSkillCores();
         if (!skillCores.isEmpty()) {
            for (Entry<Integer, Integer> entry : skillCores.entrySet()) {
               int coreId = entry.getKey();

               for (Pair<Integer, Integer> pair : HexaMatrixConstants.reqForActivation.get(coreId)) {
                  if (chr.getVCoreSkillPureLevel(pair.left) < pair.right) {
                     this.coreDisabled.put(coreId, true);
                     break;
                  }
               }
            }
         }
      }
   }

   public static class HexaStatData {
      int playerid;
      Map<Integer, HexaCore.HexaStatInfo> skillData = new HashMap<>();
      int coreid;
      int index;
      boolean changed = false;

      private HexaStatData(HexaCore.HexaStatData hexaStatData) {
         this.playerid = hexaStatData.playerid;
         this.coreid = hexaStatData.coreid;
         this.index = hexaStatData.index;

         for (Entry<Integer, HexaCore.HexaStatInfo> entry : hexaStatData.skillData.entrySet()) {
            this.skillData.put(entry.getKey(), new HexaCore.HexaStatInfo(entry.getValue().type, entry.getValue().level));
         }

         this.changed = true;
      }

      public HexaStatData(int playerid, int index) {
         this.playerid = playerid;
         this.coreid = HexaMatrixConstants.getHexaStatCoreIdByIndex(index);
         this.index = index;

         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `hexa_stats` WHERE `player_id` = ? AND `index` = ?");
         ) {
            ps.setInt(1, playerid);
            ps.setInt(2, index);

            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                  HexaMatrixConstants.HexaStatOption opt = HexaMatrixConstants.HexaStatOption.findByValue(rs.getInt("type"));
                  if (opt != null) {
                     int pos = rs.getByte("pos");
                     HexaCore.HexaStatInfo info = new HexaCore.HexaStatInfo(opt, rs.getInt("level"));
                     this.skillData.put(pos, info);
                  }
               }
            }
         } catch (SQLException var15) {
            var15.printStackTrace();
         }
      }

      public HexaCore.HexaStatData clone() {
         return new HexaCore.HexaStatData(this);
      }

      public void saveHexaStat() {
         try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `hexa_stats` WHERE `player_id` = ? AND `index` = ?")) {
               ps.setInt(1, this.playerid);
               ps.setInt(2, this.index);
               ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO `hexa_stats` (`player_id`, `index`, `pos`, `type`, `level`) VALUES(?, ?, ?, ?, ?)")) {
               ps.setInt(1, this.playerid);
               ps.setInt(2, this.index);

               for (Entry<Integer, HexaCore.HexaStatInfo> entry : this.skillData.entrySet()) {
                  ps.setInt(3, entry.getKey());
                  ps.setInt(4, entry.getValue().type.getType());
                  ps.setInt(5, entry.getValue().level);
                  ps.executeUpdate();
               }
            }
         } catch (SQLException var11) {
            var11.printStackTrace();
         }
      }

      public void resetAndAddHexaStat(int type0, int type1, int type2) {
         this.skillData.clear();
         HexaMatrixConstants.HexaStatOption opt = HexaMatrixConstants.HexaStatOption.findByValue(type0);
         if (opt != null) {
            this.skillData.put(0, new HexaCore.HexaStatInfo(opt, 0));
         }

         opt = HexaMatrixConstants.HexaStatOption.findByValue(type1);
         if (opt != null) {
            this.skillData.put(1, new HexaCore.HexaStatInfo(opt, 0));
         }

         opt = HexaMatrixConstants.HexaStatOption.findByValue(type2);
         if (opt != null) {
            this.skillData.put(2, new HexaCore.HexaStatInfo(opt, 0));
         }
      }

      public Map<Integer, HexaCore.HexaStatInfo> getStats() {
         return this.skillData;
      }

      public int getSkillLevel(HexaMatrixConstants.HexaStatOption type) {
         HexaCore.HexaStatInfo sData = this.skillData.values().stream().filter(data -> data.type == type).findFirst().orElse(null);
         return sData != null ? sData.type.getType() : 0;
      }

      public void setSkillLevel(HexaMatrixConstants.HexaStatOption type, int level) {
         for (HexaCore.HexaStatInfo info : this.skillData.values()) {
            if (info.type == type) {
               info.level = level;
            }
         }
      }

      public int getCoreId() {
         return this.coreid;
      }

      public void setIndex(int index) {
         this.index = index;
      }

      public void setChanged(boolean changed) {
         this.changed = changed;
      }
   }

   public static class HexaStatInfo {
      public HexaMatrixConstants.HexaStatOption type;
      public int level;

      public HexaStatInfo(HexaMatrixConstants.HexaStatOption type, int level) {
         this.type = type;
         this.level = level;
      }

      public void changeType(HexaMatrixConstants.HexaStatOption type) {
         this.type = type;
      }
   }
}
