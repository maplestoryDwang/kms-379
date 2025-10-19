package database.loader;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import database.DBProcessor;
import database.DBSelectionKey;
import database.callback.DBCallback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.context.friend.FriendEntry;
import objects.fields.Portal;
import objects.fields.SavedLocationType;
import objects.fields.child.union.MapleUnion;
import objects.fields.child.union.MapleUnionEntry;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.item.DamageSkinSaveData;
import objects.item.IntensePowerCrystal;
import objects.item.MapleInventoryType;
import objects.quest.MapleQuestStatus;
import objects.quest.MobQuest;
import objects.quest.QuestEx;
import objects.quest.WeeklyQuest;
import objects.users.BlackList;
import objects.users.BuyLimitEntry;
import objects.users.MapleCharacter;
import objects.users.MapleCoolDownValueHolder;
import objects.users.MapleTrait;
import objects.users.achievement.Achievement;
import objects.users.achievement.AchievementEntry;
import objects.users.achievement.AchievementInsigniaEntry;
import objects.users.extra.ExtraAbilityStatEntry;
import objects.users.looks.mannequin.MannequinEntry;
import objects.users.looks.mannequin.MannequinType;
import objects.users.looks.zero.ZeroInfo;
import objects.users.skills.LinkSkill;
import objects.users.skills.LinkSkillEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillAlarm;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillMacro;
import objects.users.skills.VCore;
import objects.users.skills.VMatrixSlot;
import objects.users.stats.HyperStat;
import objects.utils.ArrayMap;
import objects.utils.Pair;
import security.anticheat.ReportType;

public class CharacterLoader {
   public static void saveCharacter(final MapleCharacter player, final boolean fromCS, boolean disconnect) {
      DBProcessor db = DBEventManager.getNextProcessor();
      String query = "UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, secondSkincolor = ?, gender = ?, secondgender = ?, job = ?, hair = ?, basecolor = ?, addcolor = ?, baseprob = ?, secondhair = ?, face = ?, secondface = ?, demonMarking = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, fatigue = ?, charm = ?, charisma = ?, craft = ?, insight = ?, sense = ?, will = ?, totalwins = ?, totallosses = ?, pvpExp = ?, pvpPoints = ?, reborns = ?, apstorage = ?, name = ?, innerExp = ?, innerLevel = ?, soulcount = ?, itcafetime = ?, pets = ?, mesoChairCount = ?, dressUp_Clothe = ?, betaClothes = ?, stylishKill_skin = ?, hongbo_point = ?, tsd_point = ?, tsd_total_point = ?, ts_point = ?, pet_loot = ?, killpoint = ?, secondHp = ?, dance_point = ?, total_dance_point = ?, draw_elf_ear = ?, draw_tail = ?, shift = ?, frozen_link_mobID = ?, frozen_link_mobCount = ?, boss_limit_clear_1 = ?, boss_limit_clear_2 = ?, boss_limit_clear_3 = ?, enchant_point = ?, hu_failed_streak = ?, hu_last_failed_unique_id = ?, tteokguk_point = ?, lastAttendanceDate = ?, faceBaseColor = ?, faceAddColor = ?, faceBaseProb = ?, secondFaceBaseColor = ?, secondFaceAddColor = ?, secondFaceBaseProb = ?, second_base_color = ?, second_add_color = ?, second_base_prob = ?, todayContribution = ?, beta_state = ?, hyper_stat_index = ?, today_loggedin_date = ?, last_loggedin_date = ?, extra_1_option = ?, extra_1_value = ?, extra_2_option = ?, extra_2_value = ?, extra_3_option = ?, extra_3_value = ?, sub_extra_1_option = ?, sub_extra_1_value = ?, sub_extra_2_option = ?, sub_extra_2_value = ?, sub_extra_3_option = ?, sub_extra_3_value = ?, current_extra_slot = ?, current_extra_grade = ?, today_charm = ?, today_craft = ?, today_charisma = ?, today_will = ?, today_sense = ?, today_insight = ?";
      if (DBConfig.isGanglim) {
         query = query
            + ", boss_tier = ?, extra_4_option = ?, extra_4_value = ?, extra_5_option = ?, extra_5_value = ?, extra_6_option = ?, extra_6_value = ?, sub_extra_4_option = ?, sub_extra_4_value = ?, sub_extra_5_option = ?, sub_extra_5_value = ?, sub_extra_6_option = ?, sub_extra_6_value = ?, skip_intro = ?";
      }

      query = query + " WHERE id = ?";
      db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, query, new DBCallback() {
         @Override
         public void execute(PreparedStatement ps) throws SQLException {
            ZeroInfo zeroInfo = null;
            if (GameConstants.isZero(player.getJob())) {
               zeroInfo = player.getZeroInfo();
            }

            ps.setInt(1, player.getLevel());
            ps.setInt(2, player.getFame());
            ps.setShort(3, player.getStat().getStr());
            ps.setShort(4, player.getStat().getDex());
            ps.setShort(5, player.getStat().getLuk());
            ps.setShort(6, player.getStat().getInt());
            ps.setLong(7, player.getExp());
            ps.setLong(8, player.getStat().getHp() < 1L ? 50L : player.getStat().getHp());
            ps.setLong(9, zeroInfo != null ? 100L : player.getStat().getMp());
            ps.setLong(10, player.getStat().getMaxHp());
            ps.setLong(11, player.getStat().getMaxMp());
            StringBuilder sps = new StringBuilder();

            for (int i = 0; i < player.getRemainingSps().length; i++) {
               sps.append(player.getRemainingSps()[i]);
               sps.append(",");
            }

            String sp = sps.toString();
            ps.setString(12, sp.substring(0, sp.length() - 1));
            ps.setShort(13, player.getRemainingAp());
            ps.setByte(14, (byte)player.getGMLevel());
            ps.setInt(15, player.getSkinColor());
            if (zeroInfo != null) {
               ps.setInt(16, zeroInfo.getSubSkin());
            } else {
               ps.setByte(16, player.getSecondSkinColor());
            }

            ps.setByte(17, player.getGender());
            ps.setByte(18, player.getSecondGender());
            ps.setShort(19, player.getJob());
            ps.setInt(20, player.getHair());
            ps.setInt(21, player.getBaseColor());
            ps.setInt(22, player.getAddColor());
            ps.setInt(23, player.getBaseProb());
            if (zeroInfo != null) {
               ps.setInt(24, zeroInfo.getSubHair());
            } else {
               ps.setInt(24, player.getSecondHair());
            }

            ps.setInt(25, player.getFace());
            if (zeroInfo != null) {
               ps.setInt(26, zeroInfo.getSubFace());
            } else {
               ps.setInt(26, player.getSecondFace());
            }

            ps.setInt(27, player.getDemonMarking());
            if (!fromCS && player.getMap() != null) {
               if (player.getMap().getForcedReturnId() != 999999999 && player.getMap().getForcedReturnMap() != null) {
                  ps.setInt(28, player.getMap().getForcedReturnId());
               } else {
                  ps.setInt(28, player.getStat().getHp() < 1L ? player.getMap().getReturnMapId() : player.getMap().getId());
               }
            } else {
               ps.setInt(28, player.getMapId());
            }

            ps.setLong(29, player.getMeso());
            ps.setShort(30, player.getHpApUsed());
            if (player.getMap() == null) {
               ps.setByte(31, (byte)0);
            } else {
               Portal closest = player.getMap().findClosestSpawnpoint(player.getTruePosition());
               ps.setByte(31, (byte)(closest != null ? closest.getId() : 0));
            }

            ps.setInt(32, player.getParty() == null ? -1 : player.getParty().getId());
            ps.setShort(33, player.getBuddylist().getCapacity());
            ps.setByte(34, player.getSubcategory());
            ps.setInt(35, player.getMarriageId());
            ps.setInt(36, 0);
            ps.setInt(37, 0);
            ps.setShort(38, player.getFatigue());
            ps.setInt(39, player.getTrait(MapleTrait.MapleTraitType.charm).getTotalExp());
            ps.setInt(40, player.getTrait(MapleTrait.MapleTraitType.charisma).getTotalExp());
            ps.setInt(41, player.getTrait(MapleTrait.MapleTraitType.craft).getTotalExp());
            ps.setInt(42, player.getTrait(MapleTrait.MapleTraitType.insight).getTotalExp());
            ps.setInt(43, player.getTrait(MapleTrait.MapleTraitType.sense).getTotalExp());
            ps.setInt(44, player.getTrait(MapleTrait.MapleTraitType.will).getTotalExp());
            ps.setInt(45, player.getTotalWins());
            ps.setInt(46, player.getTotalLosses());
            ps.setInt(47, 0);
            ps.setInt(48, 0);
            ps.setInt(49, player.getReborns());
            ps.setInt(50, player.getAPS());
            ps.setString(51, player.getName());
            ps.setInt(52, player.getInnerExp());
            ps.setInt(53, player.getInnerLevel());
            ps.setInt(54, player.getSoulCount());
            ps.setInt(55, player.getInternetCafeTime());
            sps.delete(0, sps.toString().length());

            for (int i = 0; i < 3; i++) {
               if (player.getPet(i) != null) {
                  sps.append(player.getPet(i).getUniqueId());
               } else {
                  sps.append("-1");
               }

               sps.append(",");
            }

            sp = sps.toString();
            ps.setString(56, sp.substring(0, sp.length() - 1));
            ps.setLong(57, player.getMesoChairCount());
            ps.setInt(58, player.getDressUp_Clothe());
            if (zeroInfo != null) {
               ps.setLong(59, zeroInfo.getZeroLinkCashPart());
            } else {
               ps.setLong(59, 0L);
            }

            ps.setInt(60, player.getStylishKillSkin());
            ps.setInt(61, player.getHongboPoint());
            ps.setInt(62, player.getTSDPoint());
            ps.setInt(63, player.getTSDTotalPoint());
            ps.setInt(64, player.getTS());
            ps.setByte(65, (byte)(player.getPetLoot() ? 1 : 0));
            ps.setInt(66, player.getKillPoint());
            if (zeroInfo != null) {
               ps.setLong(67, zeroInfo.getSubHP());
            } else {
               ps.setLong(67, player.getStat().getSecondHp());
            }

            ps.setInt(68, player.getDancePoint());
            ps.setInt(69, player.getTotalDancePoint());
            ps.setInt(70, player.getDrawElfEar());
            ps.setInt(71, player.getDrawTail());
            ps.setInt(72, player.getShift());
            ps.setInt(73, player.getFrozenLinkMobID());
            ps.setInt(74, player.getFrozenLinkMobCount());
            ps.setInt(75, player.getBossLimitClear1());
            ps.setInt(76, player.getBossLimitClear2());
            ps.setInt(77, player.getBossLimitClear3());
            ps.setInt(78, player.getEnchantPoint());
            ps.setInt(79, player.getHuFailedStreak());
            ps.setLong(80, player.getHuLastFailedUniqueID());
            ps.setInt(81, player.getTteokgukPoint());
            ps.setInt(82, player.getLastAttendacneDate());
            ps.setInt(83, player.getFaceBaseColor());
            ps.setInt(84, player.getFaceAddColor());
            ps.setInt(85, player.getFaceBaseProb());
            ps.setInt(86, player.getSecondFaceBaseColor());
            ps.setInt(87, player.getSecondFaceAddColor());
            ps.setInt(88, player.getSecondFaceBaseProb());
            if (zeroInfo != null) {
               ps.setInt(89, zeroInfo.getMixBaseHairColor());
               ps.setInt(90, zeroInfo.getMixAddHairColor());
               ps.setInt(91, zeroInfo.getMixHairBaseProb());
            } else {
               ps.setInt(89, player.getSecondBaseColor());
               ps.setInt(90, player.getSecondAddColor());
               ps.setInt(91, player.getSecondBaseProb());
            }

            ps.setInt(92, player.getTodayContribution());
            if (zeroInfo == null) {
               ps.setByte(93, (byte)0);
            } else {
               ps.setByte(93, (byte)(zeroInfo.isBeta() ? 1 : 0));
            }

            ps.setInt(94, player.getHyperStat().currentIndex);
            ps.setTimestamp(95, new Timestamp(player.getTodayLoggedinDate()));
            ps.setTimestamp(96, new Timestamp(player.getLastLoggedinDate()));
            int index = 0;

            for (ExtraAbilityStatEntry e : player.getExtraAbilityStats()[0]) {
               ps.setInt(97 + index, e.getOption().getOptionID());
               ps.setInt(98 + index, e.getValue());
               index += 2;
               if (index >= 6) {
                  break;
               }
            }

            for (ExtraAbilityStatEntry ex : player.getExtraAbilityStats()[1]) {
               ps.setInt(97 + index, ex.getOption().getOptionID());
               ps.setInt(98 + index, ex.getValue());
               index += 2;
               if (index >= 12) {
                  break;
               }
            }

            ps.setInt(97 + index, player.getExtraAbilitySlot());
            ps.setInt(98 + index, player.getExtraAbilityGrade().getGradeID());
            ps.setInt(99 + index, player.getTrait(MapleTrait.MapleTraitType.charm).getTodayExp());
            ps.setInt(100 + index, player.getTrait(MapleTrait.MapleTraitType.craft).getTodayExp());
            ps.setInt(101 + index, player.getTrait(MapleTrait.MapleTraitType.charisma).getTodayExp());
            ps.setInt(102 + index, player.getTrait(MapleTrait.MapleTraitType.will).getTodayExp());
            ps.setInt(103 + index, player.getTrait(MapleTrait.MapleTraitType.sense).getTodayExp());
            ps.setInt(104 + index, player.getTrait(MapleTrait.MapleTraitType.insight).getTodayExp());
            if (DBConfig.isGanglim) {
               ps.setInt(105 + index, player.getBossTier());

               for (ExtraAbilityStatEntry exx : player.getExtraAbilityStats()[0]) {
                  index += 2;
                  if (index >= 20) {
                     ps.setInt(106 + index - 8, exx.getOption().getOptionID());
                     ps.setInt(107 + index - 8, exx.getValue());
                  }
               }

               for (ExtraAbilityStatEntry exxx : player.getExtraAbilityStats()[1]) {
                  index += 2;
                  if (index >= 32) {
                     ps.setInt(106 + index - 14, exxx.getOption().getOptionID());
                     ps.setInt(107 + index - 14, exxx.getValue());
                  }
               }

               ps.setBoolean(108 + index - 14, player.getIsSkipIntro());
               ps.setInt(109 + index - 14, player.getId());
            } else {
               ps.setInt(105 + index, player.getId());
            }
         }
      });
      if ((player.getSaveFlag() & CharacterSaveFlag.V_MATRIX.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM vcores WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO vcores (crc_id, core_id, player_id, level, exp, state, skill_1, skill_2, skill_3, position, locked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (VCore core : player.getVCoreSkillsNoLock()) {
                     ps.setLong(1, core.getCrcid());
                     ps.setInt(2, core.getCoreId());
                     ps.setInt(3, core.getCharid());
                     ps.setInt(4, core.getLevel());
                     ps.setInt(5, core.getExp());
                     ps.setInt(6, core.getState());
                     ps.setInt(7, core.getSkill1());
                     ps.setInt(8, core.getSkill2());
                     ps.setInt(9, core.getSkill3());
                     ps.setInt(10, core.getPosition());
                     ps.setInt(11, core.isLocked() ? 1 : 0);
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.V_MATRIX.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.V_MATRIX_SLOTS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM vmatrixslot WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO vmatrixslot (player_id, `index`, slot_enforcement, released) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (VMatrixSlot slot : new ArrayList<>(player.getVMatrixSlots())) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, slot.getIndex());
                     ps.setInt(3, slot.getSlotEnforcement());
                     ps.setInt(4, slot.getReleased());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.V_MATRIX_SLOTS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.STOLEN_SKILLS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM stolen WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO stolen (characterid, skillid, chosen) VALUES (?, ?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (Pair<Integer, Integer> st : player.getStolenSkills()) {
                  ps.setInt(1, player.getId());
                  ps.setInt(2, st.left);
                  ps.setInt(3, st.right);
                  ps.addBatch();
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.STOLEN_SKILLS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.SKILL_MACROS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM skillmacros WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (int next = 0; next < 5; next++) {
                     SkillMacro macro = player.getMacros()[next];
                     if (macro != null) {
                        ps.setInt(1, player.getId());
                        ps.setInt(2, macro.getSkill1());
                        ps.setInt(3, macro.getSkill2());
                        ps.setInt(4, macro.getSkill3());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getShout());
                        ps.setInt(7, next);
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.SKILL_MACROS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.KEY_VALUES.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, String.format("DELETE FROM keyvalue WHERE %s = ?", DBConfig.isGanglim ? "id" : "cid"), new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            String.format("INSERT INTO keyvalue (`%s`, `key`, `value`) VALUES (?, ?, ?)", DBConfig.isGanglim ? "id" : "cid"),
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (Entry<String, String> keyset : player.getCustomValues().entrySet()) {
                     ps.setInt(1, player.getId());
                     ps.setString(2, keyset.getKey());
                     ps.setString(3, keyset.getValue() == null ? "null" : keyset.getValue());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.KEY_VALUES.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.KEY_VALUES2.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM keyvalue2 WHERE cid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO keyvalue2 (`cid`, `key`, `value`) VALUES (?, ?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (Entry<String, Integer> keyset : player.getCustomValues2().entrySet()) {
                  ps.setInt(1, player.getId());
                  ps.setString(2, keyset.getKey());
                  ps.setLong(3, keyset.getValue().intValue());
                  ps.addBatch();
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.KEY_VALUES2.getFlag());
      }

      db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM inventoryslot WHERE characterid = ?", new DBCallback() {
         @Override
         public void execute(PreparedStatement ps) throws SQLException {
            ps.setInt(1, player.getId());
         }
      });
      db.addQuery(
         DBSelectionKey.INSERT_OR_UPDATE,
         "INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`, `cash_equip`) VALUES (?, ?, ?, ?, ?, ?, ?)",
         new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
               ps.setInt(2, player.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
               ps.setInt(3, player.getInventory(MapleInventoryType.USE).getSlotLimit());
               ps.setInt(4, player.getInventory(MapleInventoryType.SETUP).getSlotLimit());
               ps.setInt(5, player.getInventory(MapleInventoryType.ETC).getSlotLimit());
               ps.setInt(6, player.getInventory(MapleInventoryType.CASH).getSlotLimit());
               ps.setInt(7, player.getInventory(MapleInventoryType.CASH_EQUIP).getSlotLimit());
            }
         }
      );
      (new Runnable() {
            @Override
            public void run() {
               DBConnection dbx = new DBConnection();
               Connection con = DBConnection.getConnection();

               try {
                  con.setTransactionIsolation(1);
                  con.setAutoCommit(false);
                  player.saveInventory(con);
                  if ((player.getSaveFlag() & CharacterSaveFlag.CABINET.getFlag()) > 0) {
                     ItemLoader.CABINET.saveCabinetItems(player.getCabinet(), player.getAccountID());
                     player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.CABINET.getFlag());
                  }

                  for (int next = 0; next < player.getPets().length; next++) {
                     if (player.getPets()[next] != null) {
                        player.getPets()[next].saveToDb();
                     }
                  }

                  if (DBConfig.isGanglim) {
                     player.getClient().saveKeyValueToDB(con);
                  }

                  MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", player.getId());
                  PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)",
                     1
                  );
                  PreparedStatement pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
                  ps.setInt(1, player.getId());

                  for (MapleQuestStatus q : new HashMap<>(player.getQuest_Map()).values()) {
                     ps.setInt(2, q.getQuest().getId());
                     ps.setInt(3, q.getStatus());
                     ps.setInt(4, (int)(q.getCompletionTime() / 1000L));
                     ps.setInt(5, q.getForfeited());
                     ps.setString(6, q.getCustomData());
                     ps.execute();
                     ResultSet rs = ps.getGeneratedKeys();
                     if (q.hasMobKills()) {
                        rs.next();

                        for (int mob : q.getMobKills().keySet()) {
                           pse.setLong(1, rs.getLong(1));
                           pse.setInt(2, mob);
                           pse.setInt(3, q.getMobKills(mob));
                           pse.execute();
                        }
                     }

                     rs.close();
                  }

                  ps.close();
                  pse.close();
                  if (player.getStorage() != null) {
                     player.getStorage().saveToDB(con);
                  }

                  if (player.getCashShop() != null) {
                     player.getCashShop().save();
                  }

                  PlayerNPC.updateByCharId(player);

                  for (int i = 0; i < player.getKeyLayout().length; i++) {
                     player.getKeyLayout()[i].saveKeys(i, player.getId());
                  }

                  player.getMount().saveMount(player.getId());
               } catch (SQLException var18) {
                  System.out.println("CharacterLoader 저장 함수 오류 발생 코드 - 1");
                  var18.printStackTrace();
               } finally {
                  if (con != null) {
                     try {
                        con.setAutoCommit(true);
                        con.setTransactionIsolation(4);
                        con.close();
                        Connection var20 = null;
                     } catch (SQLException var17) {
                     }
                  }
               }
            }
         })
         .run();
      if ((player.getSaveFlag() & CharacterSaveFlag.MOB_QUEST.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM mob_quest WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO mob_quest (`player_id`, `quest_id`, `mob_id`, `need_count`, `mob_count`) VALUES (?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (Entry<Integer, List<MobQuest>> q : player.getMobQuests().entrySet()) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, q.getKey());

                     for (MobQuest q_ : q.getValue()) {
                        ps.setInt(3, q_.getMobID());
                        ps.setInt(4, q_.getNeedCount());
                        ps.setInt(5, q_.getMobCount());
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.MOB_QUEST.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.ACHIEVEMENT.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `achievement_info` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         final Achievement achievement = player.getAchievement();
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE,
            "INSERT INTO `achievement_info` (`account_id`, `last_week_score`, `last_week_rank`, `last_week_delta_rank`) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());
                  ps.setInt(2, achievement.getLastWeekScore());
                  ps.setInt(3, achievement.getLastWeekRank());
                  ps.setInt(4, achievement.getLastWeekDeltaRank());
               }
            }
         );
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `achievement_missions` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `achievement_missions` (`account_id`, `achievement_id`, `mission`, `status`, `time`, `sub_mission`) VALUES (?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());

                  for (AchievementEntry entry : new ArrayList<>(achievement.getAchievements())) {
                     ps.setInt(2, entry.getAchievementID());
                     ps.setByte(3, (byte)entry.getMission());
                     ps.setByte(4, (byte)entry.getStatus().getStatus());
                     ps.setTimestamp(5, new Timestamp(entry.getTime()));
                     ps.setString(6, entry.getSubMission());
                     ps.addBatch();
                  }
               }
            }
         );
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `achievement_insignia` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `achievement_insignia` (`account_id`, `grade`, `status`, `achieve_time`) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());

                  for (AchievementInsigniaEntry entry : new ArrayList<>(achievement.getAchievementInsignias())) {
                     ps.setByte(2, (byte)entry.getGrade().getGrade());
                     ps.setByte(3, (byte)entry.getStatus());
                     ps.setTimestamp(4, new Timestamp(entry.getAchieveTime()));
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag2() & ~CharacterSaveFlag2.ACHIEVEMENT.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.WEEKLY_QUEST.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM weekly_quest WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO weekly_quest (`player_id`, `quest_id`, `type`, `need_id`, `need_quantity`, `quantity`) VALUES (?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (WeeklyQuest q : player.getWeeklyQuest()) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, q.getQuestID());
                     ps.setInt(3, q.getType());
                     ps.setInt(4, q.getNeedID());
                     ps.setInt(5, q.getNeedQuantity());
                     ps.setInt(6, q.getQuantity());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.WEEKLY_QUEST.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.QUEST_INFO.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM questinfo WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM questinfo_account WHERE account_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO questinfo (`characterid`, `quest`, `customData`, `date`) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (Entry<Integer, QuestEx> q : player.getQuestInfos().entrySet()) {
                     if (!ServerConstants.isAccountShareQuestEx(q.getKey())) {
                        ps.setInt(2, q.getKey());
                        ps.setString(3, q.getValue().getData());
                        ps.setString(4, q.getValue().getTime());
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO questinfo_account (`account_id`, `quest`, `customData`, `date`) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());

                  for (Entry<Integer, QuestEx> q : player.getQuestInfos().entrySet()) {
                     if (ServerConstants.isAccountShareQuestEx(q.getKey())) {
                        ps.setInt(2, q.getKey());
                        ps.setString(3, q.getValue().getData());
                        ps.setString(4, q.getValue().getTime());
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.QUEST_INFO.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.SKILLS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM skills WHERE characterid = ?", (DBCallback)ps -> ps.setInt(1, player.getId()));
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)",
            (DBCallback)ps -> {
               for (Entry<Skill, SkillEntry> skill : player.getSkills().entrySet()) {
                  if (GameConstants.isApplicableSkill(skill.getKey().getId())) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, skill.getKey().getId());
                     ps.setInt(3, skill.getValue().skillevel);
                     ps.setByte(4, skill.getValue().masterlevel);
                     ps.setLong(5, skill.getValue().expiration);
                     ps.addBatch();
                  }
               }
            }
         );
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM hyper_stats WHERE player_id = ?", (DBCallback)ps -> ps.setInt(1, player.getId()));
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO hyper_stats (player_id, `index`, skill_id, skill_level) VALUES (?, ?, ?, ?)",
            (DBCallback)ps -> {
               for (HyperStat.HyperStatData hyperStat : player.getHyperStat().statDataList) {
                  for (HyperStat.HyperStatInfo info : hyperStat.skillData) {
                     if (info.skillLV > 0) {
                        ps.setInt(1, player.getId());
                        ps.setInt(2, hyperStat.index);
                        ps.setInt(3, info.skillID);
                        ps.setInt(4, info.skillLV);
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.SKILLS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.INNER_SKILL.getFlag()) > 0) {
         if (player.getInnerSkills() != null) {
            db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM inner_ability_skills WHERE player_id = ?", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());
               }
            });
            db.addQuery(
               DBSelectionKey.INSERT_OR_UPDATE_BATCH,
               "INSERT INTO inner_ability_skills (player_id, skill_id, skill_level, max_level, rank, locked) VALUES (?, ?, ?, ?, ?, ?)",
               new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setInt(1, player.getId());

                     for (int next = 0; next < player.getInnerSkills().size(); next++) {
                        ps.setInt(2, player.getInnerSkills().get(next).getSkillId());
                        ps.setInt(3, player.getInnerSkills().get(next).getSkillLevel());
                        ps.setInt(4, player.getInnerSkills().get(next).getMaxLevel());
                        ps.setInt(5, player.getInnerSkills().get(next).getRank());
                        ps.setBoolean(6, player.getInnerSkills().get(next).isLocked());
                        ps.addBatch();
                     }
                  }
               }
            );
         }

         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.INNER_SKILL.getFlag());
      }

      final List<MapleCoolDownValueHolder> cd = player.getCooldowns();
      if (disconnect && cd.size() > 0) {
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (MapleCoolDownValueHolder cooling : cd) {
                     ps.setInt(2, cooling.skillId);
                     ps.setLong(3, cooling.startTime);
                     ps.setLong(4, cooling.length);
                     ps.addBatch();
                  }
               }
            }
         );
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM damage_skin_save WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         if (player.getDamageSkinSaveInfo().getSaveDamageSkinDatas().size() > 0) {
            List<String> ret = new ArrayList<>();

            for (DamageSkinSaveData data : player.getDamageSkinSaveInfo().getSaveDamageSkinDatas()) {
               ret.add(String.valueOf(data.getDamageSkinID()));
            }

            final String value = String.join(",", ret);
            if (!value.isEmpty()) {
               db.addQuery(
                  DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO damage_skin_save (`player_id`, `data`, `slotCount`) VALUES (?, ?, ?)", new DBCallback() {
                     @Override
                     public void execute(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, player.getId());
                        ps.setString(2, value);
                        ps.setInt(3, player.getDamageSkinSaveInfo().getSlotCount());
                        ps.addBatch();
                     }
                  }
               );
            }

            player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());
         }
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.QUICK_SLOT_KEY_MAPPED.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM quick_slot_key_mapped WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         if (player.getQuickSlotKeyMapped().size() > 0) {
            List<String> ret = new ArrayList<>();

            for (Integer key : player.getQuickSlotKeyMapped()) {
               ret.add(String.valueOf(key));
            }

            final String value = String.join(",", ret);
            if (!value.isEmpty()) {
               db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO quick_slot_key_mapped (`player_id`, `data`) VALUES (?, ?)", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setInt(1, player.getId());
                     ps.setString(2, value);
                     ps.addBatch();
                  }
               });
            }

            player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.QUICK_SLOT_KEY_MAPPED.getFlag());
         }
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.SAVED_LOCATION.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM savedlocations WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (SavedLocationType savedLocationType : SavedLocationType.values()) {
                     if (player.getSavedLocations()[savedLocationType.getValue()] != -1) {
                        ps.setInt(2, savedLocationType.getValue());
                        ps.setInt(3, player.getSavedLocations()[savedLocationType.getValue()]);
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.SAVED_LOCATION.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.REPORTS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM reports WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO reports VALUES(DEFAULT, ?, ?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());

               for (Entry<ReportType, Integer> achid : player.getReports().entrySet()) {
                  ps.setByte(2, achid.getKey().i);
                  ps.setInt(3, achid.getValue());
                  ps.addBatch();
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.REPORTS.getFlag());
      }

      db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM buddies WHERE accid = ?", new DBCallback() {
         @Override
         public void execute(PreparedStatement ps) throws SQLException {
            ps.setInt(1, player.getAccountID());
         }
      });
      db.addQuery(
         DBSelectionKey.INSERT_OR_UPDATE_BATCH,
         "INSERT INTO buddies (accid, `buddyid`, `buddyaccid`, `pending`, `groupname`, `memo`) VALUES (?, ?, ?, 0, ?, ?)",
         new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());

               for (FriendEntry entry : player.getBuddylist().getBuddies()) {
                  if (entry.isVisible()) {
                     ps.setInt(2, entry.getCharacterId());
                     ps.setInt(3, entry.getAccountId());
                     ps.setString(4, entry.getGroupName());
                     ps.setString(5, entry.getMemo());
                     ps.addBatch();
                  }
               }
            }
         }
      );
      db.addQuery(
         DBSelectionKey.INSERT_OR_UPDATE,
         "UPDATE accounts SET `nxCredit` = ?, `ACash` = ?, `mPoints` = ?, `points` = ?, `realCash` = ?, `level_point` = ?, `hongbo_point` = ? WHERE id = ?",
         new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getNXCredit());
               ps.setInt(2, player.getACash());
               ps.setInt(3, player.getMaplePoints());
               ps.setInt(4, player.getPoints());
               ps.setInt(5, player.getRealCash());
               ps.setInt(6, player.getExtremeLevelPoint());
               ps.setInt(7, player.getHongboPoint());
               ps.setInt(8, player.getClient().getAccID());
            }
         }
      );
      if ((player.getSaveFlag() & CharacterSaveFlag.WISH_LIST.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM wishlist WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO wishlist(characterid, sn) VALUES(?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (int next = 0; next < player.getWishlistSize(); next++) {
                  ps.setInt(1, player.getId());
                  ps.setInt(2, player.getWishlist()[next]);
                  ps.addBatch();
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.WISH_LIST.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.TROCK_LOCATIONS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM trocklocations WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO trocklocations (characterid, mapid) VALUES (?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (int next = 0; next < player.getRocks().length; next++) {
                  if (player.getRocks()[next] != 999999999) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, player.getRocks()[next]);
                     ps.addBatch();
                  }
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.TROCK_LOCATIONS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.REGISTER_ROCK_LOCATIONS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM regrocklocations WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO regrocklocations (characterid, mapid) VALUES (?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (int next = 0; next < player.getRegRocks().length; next++) {
                  if (player.getRegRocks()[next] != 999999999) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, player.getRegRocks()[next]);
                     ps.addBatch();
                  }
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.REGISTER_ROCK_LOCATIONS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.HYPER_ROCK_LOCATIONS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM hyperrocklocations WHERE characterid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO hyperrocklocations (characterid, mapid) VALUES (?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               for (int next = 0; next < player.getHyperRocks().length; next++) {
                  if (player.getHyperRocks()[next] != 999999999) {
                     ps.setInt(1, player.getId());
                     ps.setInt(2, player.getHyperRocks()[next]);
                     ps.addBatch();
                  }
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.HYPER_ROCK_LOCATIONS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.EXTENDED_SLOTS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `extendedslots` WHERE `characterid` = ?", (DBCallback)ps -> ps.setInt(1, player.getId()));
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO `extendedslots` (`characterid`, `itemId`) VALUES (?, ?)", (DBCallback)ps -> {
            ps.setInt(1, player.getId());

            for (Entry<Byte, List<Integer>> entry : player.getExtendedSlots().entrySet()) {
               if (entry.getValue() != null) {
                  entry.getValue().forEach(itemID -> {
                     try {
                        ps.setInt(2, itemID);
                        ps.addBatch();
                     } catch (SQLException var3x) {
                        var3x.printStackTrace();
                     }
                  });
               }
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.EXTENDED_SLOTS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.LINK_SKILLS.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM linkskill WHERE accid = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO linkskill (`realskillid`, `skillid`, `linking_cid`, `linked_cid`, `skilllevel`, `time`, `accid`) VALUES (?, ?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  for (LinkSkillEntry entry : player.getLinkSkill().getLinkSkills()) {
                     ps.setInt(1, entry.getRealSkillID());
                     ps.setInt(2, entry.getSkillID());
                     ps.setInt(3, entry.getLinkingPlayerID());
                     ps.setInt(4, entry.getLinkedPlayerID());
                     ps.setInt(5, entry.getSkillLevel());
                     ps.setLong(6, entry.getLinkedTime());
                     ps.setInt(7, player.getAccountID());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.LINK_SKILLS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.AUCTION_WISH_LIST.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM auction_wishlist WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "INSERT INTO auction_wishlist (`player_id`, `wishlist`) VALUES (?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               List<String> list = new ArrayList<>();

               for (Integer id : player.getAuctionWishList()) {
                  list.add(String.valueOf(id));
               }

               String arr = String.join(",", list);
               ps.setInt(1, player.getId());
               ps.setString(2, arr);
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.AUCTION_WISH_LIST.getFlag());
      }

      final MapleUnion union = player.getMapleUnion();
      if ((player.getSaveFlag() & CharacterSaveFlag.MAPLE_UNION_DATA.getFlag()) > 0) {
         if (union != null) {
            db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM maple_union_data WHERE account_id = ?", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());
               }
            });
            db.addQuery(
               DBSelectionKey.INSERT_OR_UPDATE, "INSERT INTO maple_union_data (`account_id`, `rank`, `current_preset`) VALUES (?, ?, ?)", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setInt(1, player.getAccountID());
                     ps.setInt(2, union.rank);
                     ps.setInt(3, union.currentPreset);
                  }
               }
            );
         }

         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.MAPLE_UNION_DATA.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.MAPLE_UNION_GROUP.getFlag()) > 0) {
         if (union != null) {
            db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM maple_union_group WHERE account_id = ?", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());
               }
            });
            db.addQuery(
               DBSelectionKey.INSERT_OR_UPDATE_BATCH,
               "INSERT INTO maple_union_group (`account_id`, `preset`, `changeable_group`) VALUES (?, ?, ?)",
               new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setInt(1, player.getAccountID());
                     List<String> list = new ArrayList<>();

                     for (MapleUnion union_ : player.getMapleUnionPreset()) {
                        list.clear();
                        if (!union_.changeableGroup.isEmpty()) {
                           for (Integer id : union_.changeableGroup) {
                              list.add(String.valueOf(id));
                           }

                           String arr = String.join(",", list);
                           ps.setInt(2, union_.currentPreset);
                           ps.setString(3, arr);
                           ps.addBatch();
                        }
                     }
                  }
               }
            );
         }

         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.MAPLE_UNION_GROUP.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.MAPLE_UNION_RAIDERS.getFlag()) > 0) {
         if (union != null) {
            db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM maple_union_raiders WHERE account_id = ?", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());
               }
            });
            db.addQuery(
               DBSelectionKey.INSERT_OR_UPDATE_BATCH,
               "INSERT INTO maple_union_raiders (`account_id`, `type`, `preset`, `player_id`, `angle`, `board`) VALUES (?, ?, ?, ?, ?, ?)",
               new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     ps.setInt(1, player.getAccountID());

                     for (MapleUnion union_ : player.getMapleUnionPreset()) {
                        for (MapleUnionEntry entry : union_.activeRaiders) {
                           ps.setInt(2, entry.type);
                           ps.setInt(3, union_.currentPreset);
                           ps.setInt(4, entry.characterID);
                           ps.setInt(5, entry.angle);
                           ps.setInt(6, entry.board);
                           ps.addBatch();
                        }
                     }
                  }
               }
            );
         }

         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.MAPLE_UNION_RAIDERS.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.WILD_HUNTER_INFO.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM wild_hunter_info WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE,
            "INSERT INTO wild_hunter_info (`player_id`, `riding_type`, `captured_mob_1`, `captured_mob_2`, `captured_mob_3`, `captured_mob_4`, `captured_mob_5`) VALUES (?, ?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());
                  ps.setInt(2, player.getWildHunterInfo().getRidingType());

                  for (int i = 0; i < 5; i++) {
                     ps.setInt(3 + i, player.getWildHunterInfo().getCapturedMob(i));
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.WILD_HUNTER_INFO.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.NPC_SHOP_BUY_LIMIT.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM shop_buy_limit WHERE player_id = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO shop_buy_limit (`player_id`, `shop_id`, `item_index`, `item_id`, `buy_count`, `buy_time`) VALUES (?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (BuyLimitEntry entry : new ArrayList<>(player.getBuyLimit().getBuyLimits())) {
                     ps.setInt(2, entry.getShopID());
                     ps.setInt(3, entry.getItemIndex());
                     ps.setInt(4, entry.getItemID());
                     ps.setInt(5, entry.getBuyCount());
                     ps.setTimestamp(6, new Timestamp(entry.getBuyTime()));
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.NPC_SHOP_BUY_LIMIT.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.NPC_SHOP_WORLD_BUY_LIMIT.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM shop_world_buy_limit WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO shop_world_buy_limit (`account_id`, `shop_id`, `item_index`, `item_id`, `buy_count`, `buy_time`) VALUES (?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());

                  for (BuyLimitEntry entry : new ArrayList<>(player.getWorldBuyLimit().getBuyLimits())) {
                     ps.setInt(2, entry.getShopID());
                     ps.setInt(3, entry.getItemIndex());
                     ps.setInt(4, entry.getItemID());
                     ps.setInt(5, entry.getBuyCount());
                     ps.setTimestamp(6, new Timestamp(entry.getBuyTime()));
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.NPC_SHOP_WORLD_BUY_LIMIT.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.PRAISE_POINT.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `praise_point` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "INSERT INTO `praise_point` (`account_id`, `total_point`, `point`) VALUES (?, ?, ?)", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
               ps.setInt(2, player.getPraisePoint().getTotalPoint());
               ps.setInt(3, player.getPraisePoint().getPoint());
            }
         });
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.PRAISE_POINT.getFlag());
      }

      if ((player.getSaveFlag() & CharacterSaveFlag.CONSUME_ITEM_LIMIT.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `consume_item_limit` WHERE `player_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `consume_item_limit` (`player_id`, `item_id`, `limit_time`) VALUES (?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (Entry<Integer, Long> entry : player.getConsumeItemLimits().entrySet()) {
                     ps.setInt(2, entry.getKey());
                     ps.setTimestamp(3, new Timestamp(entry.getValue()));
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag(player.getSaveFlag() & ~CharacterSaveFlag.CONSUME_ITEM_LIMIT.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.BLACK_LIST.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `blacklist` WHERE `characterId` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `blacklist` (`characterId`, `b_chrName`, `b_denoteName`, `b_chrId`, `b_unk`) VALUES (?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());

                  for (BlackList blackList : player.getBlackLists()) {
                     ps.setString(2, blackList.getName());
                     ps.setString(3, blackList.getDenoteName());
                     ps.setInt(4, blackList.getId());
                     ps.setInt(5, blackList.getUnk());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag() & ~CharacterSaveFlag2.BLACK_LIST.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `intense_power_crystal` WHERE `player_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `intense_power_crystal` (`player_id`, `item_unique_id`, `mob_id`, `member_count`, `price`, `unk`, `gain_time`) VALUES (?, ?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  Map<Long, IntensePowerCrystal> clone = new ArrayMap<>();

                  for (Long key : player.getIntensePowerCrystals().keySet()) {
                     if (key != null) {
                        IntensePowerCrystal value = player.getIntensePowerCrystals().get(key);
                        if (value != null) {
                           clone.put(key, value);
                        }
                     }
                  }

                  ps.setInt(1, player.getId());

                  for (Entry<Long, IntensePowerCrystal> entry : clone.entrySet()) {
                     ps.setLong(2, entry.getKey());
                     IntensePowerCrystal ipc = entry.getValue();
                     ps.setInt(3, ipc.getMobID());
                     ps.setInt(4, ipc.getMemberCount());
                     ps.setLong(5, ipc.getPrice());
                     ps.setLong(6, ipc.getUnk());
                     ps.setLong(7, ipc.getGainTime());
                     ps.addBatch();
                  }
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag() & ~CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.MANNEQUIN_SLOT_MAX.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `mannequin_slotmax` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH, "INSERT INTO `mannequin_slotmax` (`account_id`, `type`, `slot_max`) VALUES (?, ?, ?)", new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());
                  ps.setByte(2, (byte)MannequinType.HairRoom.getType());
                  ps.setByte(3, (byte)player.getHairMannequin().getSlotMax());
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag2() & ~CharacterSaveFlag2.MANNEQUIN_SLOT_MAX.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.MANNEQUIN.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `mannequin` WHERE `account_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getAccountID());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `mannequin` (`account_id`, `type`, `item_id`, `base_color`, `add_color`, `base_prob`) VALUES (?, ?, ?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getAccountID());

                  for (MannequinEntry entry : player.getHairMannequin().getRooms()) {
                     if (entry != null) {
                        ps.setByte(2, (byte)MannequinType.HairRoom.getType());
                        ps.setInt(3, entry.getItemID());
                        ps.setByte(4, entry.getBaseColor());
                        ps.setByte(5, entry.getAddColor());
                        ps.setByte(6, entry.getBaseProb());
                        ps.addBatch();
                     }
                  }

                  for (MannequinEntry entryx : player.getFaceMannequin().getRooms()) {
                     if (entryx != null) {
                        ps.setByte(2, (byte)MannequinType.FaceRoom.getType());
                        ps.setInt(3, entryx.getItemID());
                        ps.setByte(4, entryx.getBaseColor());
                        ps.setByte(5, entryx.getAddColor());
                        ps.setByte(6, entryx.getBaseProb());
                        ps.addBatch();
                     }
                  }

                  for (MannequinEntry entryxx : player.getSkinMannequin().getRooms()) {
                     if (entryxx != null) {
                        ps.setByte(2, (byte)MannequinType.SkinRoom.getType());
                        ps.setInt(3, entryxx.getItemID());
                        ps.setByte(4, entryxx.getBaseColor());
                        ps.setByte(5, entryxx.getAddColor());
                        ps.setByte(6, entryxx.getBaseProb());
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag2() & ~CharacterSaveFlag2.MANNEQUIN.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.SKILL_ALARM.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `skill_alarm` WHERE `character_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `skill_alarm` (`character_id`, `alarm_list`, `alarm_onoff`) VALUES (?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());
                  SkillAlarm alarm = player.getSkillAlarm();
                  StringBuilder sb = new StringBuilder();

                  for (int i = 0; i < alarm.getSkillList().length; i++) {
                     sb.append(alarm.getSkillList()[i]);
                     sb.append(",");
                  }

                  String str = sb.toString();
                  ps.setString(2, str.substring(0, str.length() - 1));
                  sb = new StringBuilder();

                  for (int i = 0; i < alarm.getOnOffCheck().length; i++) {
                     sb.append(alarm.getOnOffCheck()[i] ? "1" : "0");
                     sb.append(",");
                  }

                  str = sb.toString();
                  ps.setString(3, str.substring(0, str.length() - 1));
                  ps.addBatch();
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag2() & ~CharacterSaveFlag2.SKILL_ALARM.getFlag());
      }

      if ((player.getSaveFlag2() & CharacterSaveFlag2.LINKSKILL_PRESET.getFlag()) > 0) {
         db.addQuery(DBSelectionKey.INSERT_OR_UPDATE, "DELETE FROM `linkskill_preset` WHERE `character_id` = ?", new DBCallback() {
            @Override
            public void execute(PreparedStatement ps) throws SQLException {
               ps.setInt(1, player.getId());
            }
         });
         db.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "INSERT INTO `linkskill_preset` (`character_id`, `skill_id`, `linking_cid`, `preset`) VALUES (?, ?, ?, ?)",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  ps.setInt(1, player.getId());
                  LinkSkill linkSkill = player.getLinkSkill();
                  List<List<LinkSkillEntry>> presetList = linkSkill.getLinkSkillPresets();

                  for (List<LinkSkillEntry> skillList : presetList) {
                     for (LinkSkillEntry skill : skillList) {
                        ps.setInt(2, skill.getRealSkillID());
                        ps.setInt(3, skill.getLinkingPlayerID());
                        ps.setInt(4, presetList.indexOf(skillList));
                        ps.addBatch();
                     }
                  }
               }
            }
         );
         player.setSaveFlag2(player.getSaveFlag2() & ~CharacterSaveFlag2.LINKSKILL_PRESET.getFlag());
      }

      if (player.getHexaCore() != null) {
         player.getHexaCore().saveCheck();
      }
   }
}
