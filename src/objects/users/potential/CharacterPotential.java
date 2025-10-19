package objects.users.potential;

import constants.GameConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class CharacterPotential {
   private static CharacterPotential instance = null;
   private static List<Pair<Integer, CharacterPotentialSkillLevel>> skillLevelTables;

   public static CharacterPotential getInstance() {
      if (instance == null) {
         instance = new CharacterPotential();
      }

      return instance;
   }

   public static void loadSkillLevelTables() {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM `character_potential_table`");
         ResultSet rs = ps.executeQuery();
         skillLevelTables = new ArrayList<>();

         while (rs.next()) {
            int grade = rs.getInt("rare");
            int skillID = rs.getInt("skill_id");
            String v = rs.getString("skill_point");
            String[] args = v.split(",");
            int[] skillLevels = new int[args.length];
            int idx = 0;

            for (String arg : args) {
               skillLevels[idx++] = Integer.parseInt(arg);
            }

            skillLevelTables.add(new Pair<>(grade, new CharacterPotentialSkillLevel(skillID, new CharacterPotentialSkillLevelEntry(grade, skillLevels))));
         }

         rs.close();
         ps.close();
      } catch (SQLException var15) {
      }

      System.out.println("어빌리티 스킬 레벨 데이터가 캐싱되었습니다.");
   }

   public CharacterPotentialHolder renewSkill(List<CharacterPotentialHolder> inners, List<CharacterPotentialHolder> newValues, int rank, boolean locked) {
      return this.renewSkill(inners, newValues, rank, false, locked);
   }

   public CharacterPotentialHolder renewSkill(
      List<CharacterPotentialHolder> inners, List<CharacterPotentialHolder> newValues, int rank, boolean ultimateCirculatorPos, boolean locked
   ) {
      return this.getRandomSkill(inners, newValues, rank, locked, ultimateCirculatorPos);
   }

   public CharacterPotentialHolder getRandomSkill(
      List<CharacterPotentialHolder> inners, List<CharacterPotentialHolder> newValues, int grade, boolean locked, boolean ultimateCirculatorPos
   ) {
      List<Pair<Integer, Integer>> skillList = this.getCharacterPotentialSkillListByGrade(grade);

      for (CharacterPotentialHolder holder : newValues) {
         for (Pair<Integer, Integer> pair : new ArrayList<>(skillList)) {
            if (pair.left == holder.getSkillId()) {
               skillList.remove(pair);
            }
         }
      }

      int randomSkill = 0;
      Collections.shuffle(skillList);
      Pair<Integer, Integer> pick = skillList.stream().findAny().orElse(null);
      if (pick != null) {
         randomSkill = pick.left;
         int skillLevel = this.getSkillLevel(grade, randomSkill);
         if (skillLevel == -1) {
            int maxLevel = pick.right;
            int minSlv = maxLevel - (4 - grade) * 10 + 1;
            int maxSlv = minSlv + 9;
            int random = Randomizer.rand(minSlv, maxSlv);
            if (ultimateCirculatorPos) {
               random = maxSlv;
            }

            skillLevel = random;
         }

         return new CharacterPotentialHolder(randomSkill, (byte)skillLevel, (byte)SkillFactory.getSkill(randomSkill).getMaxLevel(), (byte)grade, locked);
      } else {
         return null;
      }
   }

   public int getSkillLevel(int grade, int skillID) {
      for (Pair<Integer, CharacterPotentialSkillLevel> pair : skillLevelTables) {
         CharacterPotentialSkillLevel data = pair.getRight();
         if (pair.left == grade && data.getSkillID() == skillID) {
            return data.getSkillLevelForRandom();
         }
      }

      return -1;
   }

   public int getCirculatorRank(int circulator) {
      return circulator % 1000 / 100 + 1;
   }

   public List<Pair<Integer, Integer>> getCharacterPotentialSkillListByGrade(int expectedGrade) {
      List<Pair<Integer, Integer>> ret = new ArrayList<>();

      for (int skillID = 70000000; skillID < 70000062; skillID++) {
         Skill skill = SkillFactory.getSkill(skillID);
         int needSlv = (4 - expectedGrade) * 10;
         if (skill != null && !GameConstants.isBlockedInnerAbility(skillID) && skill.getMaxLevel() >= needSlv) {
            ret.add(new Pair<>(skillID, skill.getMaxLevel()));
         }
      }

      return ret;
   }
}
