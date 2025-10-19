package objects.users.achievement;

import constants.ServerConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.item.Equip;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.MapleTrait;
import objects.users.achievement.caching.AchievementData;
import objects.users.achievement.caching.AchievementGradeInfo;
import objects.users.achievement.caching.AchievementInsigniaInfo;
import objects.users.achievement.caching.mission.Mission;
import objects.users.achievement.caching.mission.submission.checkvalue.Mob;
import objects.users.achievement.caching.mission.submission.checkvalue.SkillUse;
import objects.users.achievement.caching.mission.submission.checkvalue.check.AbilityCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.AchievementCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.ComboKillGetMarbleCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.ComboKillIncreaseCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.DailygiftGetRewardCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.FieldCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.FieldLeaveCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.GuildAttendCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.GuildCommitmentIncCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.ItemUseCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.LotteryResultItemCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingSkillFatigueIncCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingskillDecompositionCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingskillGatherCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingskillLvupCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingskillMakeCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MakingskillSynthesizeCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MasterPieceSuccessCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MobKillCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MobKillOptimumCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.MultiKillCountCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.NCStatLevelUpCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.NpcShopBuyCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.PickupMobRewardItemCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.PickupMobRewardMesoCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.PickupReactorRewardItemCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.QuestCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.RuneStoneUseResultSuccessCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.SpelltraceEnchantCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.StarforceEnchantCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.SuddenMissionCompleteCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.SuddenMissionRewardCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.UserHitCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.check.UserLvUpCheck;
import objects.users.achievement.caching.mission.submission.score.Score;
import objects.users.achievement.caching.mission.submission.score.SetValueType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataFileEntry;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;

public class AchievementFactory {
   private static final MapleData info = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"))
      .getData("AchievementInfo.img");
   private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
   public static final List<AchievementGradeInfo> gradeInfos = new ArrayList<>();
   public static final List<AchievementInsigniaInfo> insigniaInfos = new ArrayList<>();
   public static final Map<Integer, AchievementData> achievementDatas = new HashMap<>();

   public static void loadData() {
      for (MapleData data : info) {
         if (data.getName().equals("Grade")) {
            for (MapleData root : data.getChildren()) {
               gradeInfos.add(new AchievementGradeInfo(root));
            }
         } else if (data.getName().equals("Insignia")) {
            for (MapleData root : data.getChildren()) {
               insigniaInfos.add(new AchievementInsigniaInfo(root));
            }
         }
      }

      for (MapleDataFileEntry d : AchievementFactory.data.getRoot("AchievementData_000.wz").getFiles()) {
         int achievementID = Integer.parseInt(d.getName().split(".img")[0]);
         AchievementData ad = new AchievementData(AchievementFactory.data.getData(d.getName()));
         achievementDatas.put(achievementID, ad);
      }
   }

   public static AchievementData getAchievementData(int achievementID) {
      return achievementDatas.getOrDefault(achievementID, null);
   }

   public static AchievementGrade getGrade(int totalScore) {
      for (AchievementGradeInfo info : gradeInfos) {
         if (!info.getGradeName().equals("master")) {
            if (info.getScoreMin() <= totalScore && info.getScoreMax() > totalScore) {
               return AchievementGrade.getGradeByName(info.getGradeName());
            }

            if (info.getGradeName().equals("diamond") && info.getScoreMin() <= totalScore) {
               return AchievementGrade.diamond;
            }
         }
      }

      return null;
   }

   public static void checkMobKill(
      MapleMonster mob, List<MapleCharacter> players, MapleCharacter lastAttacker, MapleCharacter highestDamagedAtacker, int skillID
   ) {
      if (ServerConstants.useAchievement) {
         for (MapleCharacter player : players) {
            if ((Mob.allMobList.contains(mob.getId()) || SkillUse.allSkillIDList.contains(skillID))
               && (player.getMap().getFieldSetInstance() == null || player.getBossMode() != 1)) {
               MobKillCheck mobKillCheck = new MobKillCheck(player, lastAttacker, highestDamagedAtacker, players, mob, skillID, false);
               mobKillCheck.checkMission(player, AchievementSubMissionType.mob_kill, 1L);
            }
         }
      }
   }

   public static void checkMobKillOptimum(MapleMonster mob, MapleCharacter player, int delta) {
      if (ServerConstants.useAchievement) {
         MobKillOptimumCheck mobKillOptimumCheck = new MobKillOptimumCheck(player.getLevel(), mob.getStats().getLevel());
         mobKillOptimumCheck.checkMission(player, AchievementSubMissionType.mob_kill, delta);
      }
   }

   public static void checkSpelltraceEnchant(MapleCharacter player, int scroll, int sucRate, int juhunNumber, boolean success) {
      if (ServerConstants.useAchievement) {
         SpelltraceEnchantCheck spelltraceEnchantCheck = new SpelltraceEnchantCheck(scroll, sucRate, success);
         spelltraceEnchantCheck.checkMission(player, AchievementSubMissionType.spell_trace_enchant, juhunNumber);
      }
   }

   public static void checkStarforceEnchant(MapleCharacter player, String result, int starForce, String catchResult, long meso) {
      if (ServerConstants.useAchievement) {
         StarforceEnchantCheck check = new StarforceEnchantCheck(result, starForce, catchResult, (int)meso);
         check.checkMission(player, AchievementSubMissionType.starforce_enchant, 1L);
      }
   }

   public static void checkPickupMobRewardMeso(MapleCharacter player, int meso) {
      if (ServerConstants.useAchievement) {
         PickupMobRewardMesoCheck check = new PickupMobRewardMesoCheck(meso);
         check.checkMission(player, AchievementSubMissionType.pickup_mob_reward_meso, 1L);
      }
   }

   public static void checkPickupMobRewardItem(MapleCharacter player, List<Item> items) {
      if (ServerConstants.useAchievement) {
         PickupMobRewardItemCheck check = new PickupMobRewardItemCheck(items);
         check.checkMission(player, AchievementSubMissionType.pickup_mob_reward_item, 1L);
      }
   }

   public static void checkSuddenmissionReward(MapleCharacter player, int meso) {
      if (ServerConstants.useAchievement) {
         SuddenMissionRewardCheck check = new SuddenMissionRewardCheck(meso);
         check.checkMission(player, AchievementSubMissionType.suddenmission_reward, meso);
      }
   }

   public static void checkSuddenMissionComplete(MapleCharacter player, int questid, int clearTimeSecond) {
      if (ServerConstants.useAchievement) {
         SuddenMissionCompleteCheck check = new SuddenMissionCompleteCheck(questid, clearTimeSecond);
         check.checkMission(player, AchievementSubMissionType.suddenmission_complete, 1L);
      }
   }

   public static void checkRuneStoneUseResultSuccess(MapleCharacter player, int runeNumber) {
      if (ServerConstants.useAchievement) {
         RuneStoneUseResultSuccessCheck check = new RuneStoneUseResultSuccessCheck(runeNumber);
         check.checkMission(player, AchievementSubMissionType.rune_stone_use_result_success, 1L);
      }
   }

   public static void checkQuest(MapleCharacter player, int questId, int questState) {
      if (ServerConstants.useAchievement) {
         QuestCheck questCheck = new QuestCheck(player, questId, questState);
         questCheck.checkMission(player, AchievementSubMissionType.quest_state_change, 1L);
      }
   }

   public static void checkAchievementStateChange(MapleCharacter player, int achievementID, int achievementState) {
      if (ServerConstants.useAchievement) {
         AchievementCheck check = new AchievementCheck(achievementID, achievementState);
         int delta = 1;
         check.checkMission(player, AchievementSubMissionType.achievement_state_change, delta);
      }
   }

   public static void checkAbility(MapleCharacter player, int skillID, int skillLevel, int grade, int honorExpDec) {
      if (ServerConstants.useAchievement) {
         AbilityCheck abilityCheck = new AbilityCheck(skillID, skillLevel, grade);
         abilityCheck.checkMission(player, AchievementSubMissionType.ability_change, honorExpDec);
      }
   }

   public static void checkGuildCommitmentInc(MapleCharacter player, int add) {
      if (ServerConstants.useAchievement) {
         GuildCommitmentIncCheck check = new GuildCommitmentIncCheck();
         check.checkMission(player, AchievementSubMissionType.guild_commitment_inc, add);
      }
   }

   public static void checkGuildAttendCheck(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         GuildAttendCheck guildAttendCheck = new GuildAttendCheck();
         guildAttendCheck.checkMission(player, AchievementSubMissionType.guild_attend_check, 1L);
      }
   }

   public static void checkItemUse(MapleCharacter player, int itemID, int incHP, int petSatiety) {
      if (ServerConstants.useAchievement) {
         ItemUseCheck itemUseCheck = new ItemUseCheck(itemID, incHP, petSatiety);
         itemUseCheck.checkMission(player, AchievementSubMissionType.item_use, 1L);
      }
   }

   public static void checkNpcShopBuy(MapleCharacter player, int itemID, int npcID, long meso) {
      if (ServerConstants.useAchievement) {
         NpcShopBuyCheck check = new NpcShopBuyCheck(npcID, itemID);
         check.checkMission(player, AchievementSubMissionType.npcshop_buy, meso);
      }
   }

   public static void checkMakingSkillFatigueInc(MapleCharacter player, int skillID, int fatigue) {
      if (ServerConstants.useAchievement) {
         MakingSkillFatigueIncCheck mck = new MakingSkillFatigueIncCheck(skillID, fatigue);
         mck.checkMission(player, AchievementSubMissionType.makingskill_fatigue_inc, fatigue);
      }
   }

   public static void checkMasterPieceSuccess(MapleCharacter player, int labelGrade) {
      if (ServerConstants.useAchievement) {
         MasterPieceSuccessCheck mpsc = new MasterPieceSuccessCheck(labelGrade);
         mpsc.checkMission(player, AchievementSubMissionType.master_piece_success, 1L);
      }
   }

   public static void checkDailygiftGetReward(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         DailygiftGetRewardCheck dggrc = new DailygiftGetRewardCheck();
         dggrc.checkMission(player, AchievementSubMissionType.dailygift_get_reward, 1L);
      }
   }

   public static void checkField(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         FieldCheck check = new FieldCheck(player.getMapId(), player.getMap().getFieldSetInstance());
         check.checkMission(player, AchievementSubMissionType.field_enter, 1L);
      }
   }

   public static void checkNCStatLevelUp(MapleCharacter player, MapleTrait trait) {
      if (ServerConstants.useAchievement) {
         NCStatLevelUpCheck check = new NCStatLevelUpCheck(trait);
         check.checkMission(player, AchievementSubMissionType.nc_stat_level_up, trait.getLevel());
      }
   }

   public static void checkNCStatExpUp(MapleCharacter player, MapleTrait trait) {
      if (ServerConstants.useAchievement) {
         NCStatLevelUpCheck check = new NCStatLevelUpCheck(trait);
         check.checkMission(player, AchievementSubMissionType.nc_stat_exp_up, trait.getTodayExp());
      }
   }

   public static void checkUserLvUp(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         UserLvUpCheck check = new UserLvUpCheck(player, player.getMap().getId());
         check.checkMission(player, AchievementSubMissionType.user_lvup, player.getLevel());
      }
   }

   public static void checkMakingskillSynthesize(MapleCharacter player, boolean success, Equip item1, Equip item2, Equip result) {
      if (ServerConstants.useAchievement) {
         MakingskillSynthesizeCheck msr = new MakingskillSynthesizeCheck(success, item1, item2, result);
         msr.checkMission(player, AchievementSubMissionType.makingskill_synthesize, 1L);
      }
   }

   public static void checkMakingskillDecomposition(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         MakingskillDecompositionCheck decompositionCheck = new MakingskillDecompositionCheck();
         decompositionCheck.checkMission(player, AchievementSubMissionType.makingskill_decomposition, 1L);
      }
   }

   public static void checkMakingskillGather(MapleCharacter player, boolean success, int skillID) {
      if (ServerConstants.useAchievement) {
         MakingskillGatherCheck gatherCheck = new MakingskillGatherCheck(success, skillID);
         gatherCheck.checkMission(player, AchievementSubMissionType.makingskill_gather, 1L);
      }
   }

   public static void checkMakingskillMaking(MapleCharacter player, boolean success, int skillID) {
      if (ServerConstants.useAchievement) {
         MakingskillMakeCheck makeCheck = new MakingskillMakeCheck(success, skillID);
         makeCheck.checkMission(player, AchievementSubMissionType.makingskill_making, 1L);
      }
   }

   public static void checkPickupReactorRewardItem(MapleCharacter player, int itemID) {
      if (ServerConstants.useAchievement) {
         PickupReactorRewardItemCheck rewardItemCheck = new PickupReactorRewardItemCheck(itemID);
         rewardItemCheck.checkMission(player, AchievementSubMissionType.pickup_reactor_reward_item, 1L);
      }
   }

   public static void checkMakingskillLvup(MapleCharacter player, int skillId, int lv) {
      if (ServerConstants.useAchievement) {
         MakingskillLvupCheck makingskillLvupCheck = new MakingskillLvupCheck(skillId, lv);
         makingskillLvupCheck.checkMission(player, AchievementSubMissionType.makingskill_lvup, lv);
      }
   }

   public static void checkLotteryResultItem(MapleCharacter player, int itemID, int resultItemID, int resultItemLv) {
      if (ServerConstants.useAchievement) {
         LotteryResultItemCheck lriCheck = new LotteryResultItemCheck(itemID, resultItemID, resultItemLv);
         lriCheck.checkMission(player, AchievementSubMissionType.lottery_result_item, 1L);
      }
   }

   public static void checkComboKillGetMarble(MapleCharacter player, int itemID) {
      if (ServerConstants.useAchievement) {
         ComboKillGetMarbleCheck check = new ComboKillGetMarbleCheck(itemID);
         check.checkMission(player, AchievementSubMissionType.combokill_get_marble, 1L);
      }
   }

   public static void checkComboKillIncrease(MapleCharacter player, int combo) {
      if (ServerConstants.useAchievement) {
         ComboKillIncreaseCheck check = new ComboKillIncreaseCheck(combo);
         check.checkMission(player, AchievementSubMissionType.combokill_increase, combo);
      }
   }

   public static void checkMultiKillCount(MapleCharacter player, int combo) {
      if (ServerConstants.useAchievement) {
         MultiKillCountCheck check = new MultiKillCountCheck(combo);
         check.checkMission(player, AchievementSubMissionType.multikill, combo);
      }
   }

   public static void checkFieldLeave(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         FieldLeaveCheck check = new FieldLeaveCheck();
         check.checkMission(player, AchievementSubMissionType.field_leave, 1L);
      }
   }

   public static void checkUserHitCheck(MapleCharacter player, int hitDamagePercent) {
      if (ServerConstants.useAchievement) {
         UserHitCheck check = new UserHitCheck(player, hitDamagePercent);
         check.checkMission(player, AchievementSubMissionType.user_hit, 1L);
      }
   }

   public static void resetUserHit(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         int[] aids = new int[]{558};
         List<AchievementEntry> updateList = new ArrayList<>();

         for (int aid : aids) {
            if (!player.getAchievement().checkCompleteAchievement(aid)) {
               for (AchievementEntry ae : new ArrayList<>(player.getAchievement().getAchievements())) {
                  if (ae.getAchievementID() == aid && !ae.getSubMission("mob_kill").equals("0")) {
                     ae.setSubMission("mob_kill", "0");
                     updateList.add(ae);
                  }
               }
            }
         }

         if (!updateList.isEmpty()) {
            player.getAchievement().updateAchievement(player, updateList);
         }
      }
   }

   public static void resetFieldLeave(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         if (player != null && player.getAchievement() != null) {
            int[] aids = new int[]{558};
            List<AchievementEntry> updateList = new ArrayList<>();

            for (int aid : aids) {
               if (!player.getAchievement().checkCompleteAchievement(aid)) {
                  for (AchievementEntry ae : new ArrayList<>(player.getAchievement().getAchievements())) {
                     if (ae != null && ae.getAchievementID() == aid) {
                        ae.setSubMission("mob_kill=0");
                        updateList.add(ae);
                     }
                  }
               }
            }

            if (!updateList.isEmpty()) {
               player.getAchievement().updateAchievement(player, updateList);
            }
         }
      }
   }

   public static void resetDayChange(MapleCharacter player) {
      if (ServerConstants.useAchievement) {
         int[] aids = new int[]{216, 217, 218, 219, 220};
         List<AchievementEntry> updateList = new ArrayList<>();

         for (int aid : aids) {
            if (!player.getAchievement().checkCompleteAchievement(aid)) {
               for (AchievementEntry ae : new ArrayList<>(player.getAchievement().getAchievements())) {
                  if (ae.getAchievementID() == aid) {
                     ae.setSubMission("makingskill_fatigue_inc=0");
                     updateList.add(ae);
                  }
               }
            }
         }

         if (!updateList.isEmpty()) {
            player.getAchievement().updateAchievement(player, updateList);
         }
      }
   }

   public static AchievementEntry checkMission(
      long deltaScore,
      MapleCharacter player,
      Achievement achievement,
      AchievementSubMissionType type,
      Mission mission,
      Score score,
      int achievementID,
      int subMissionID
   ) {
      AchievementEntry e = null;
      long count = 0L;
      if (!achievement.checkCompleteAchievement(achievementID)) {
         for (AchievementEntry ae : new ArrayList<>(achievement.getAchievements())) {
            if (ae.getAchievementID() == achievementID && ae.getMission() == subMissionID) {
               e = ae;

               try {
                  count = achievement.getDataFromSubMission(e.getSubMission());
               } catch (Exception var19) {
                  String subMission = ae.getSubMission();
                  if (subMission != null && !subMission.isEmpty()) {
                     System.out.println("[ParseInt 오류발생] 업적ID : " + achievementID + "서브미션 : " + ae.getSubMission());
                     String[] args = subMission.split("=");
                     if (args.length > 1 && args[1].equals("4500guild_commitment_inc")) {
                        count = 4500L;
                     }
                  }
               }
               break;
            }
         }

         if (count >= score.getTargetScore() && e != null && e.getStatus() == AchievementMissionStatus.Complete) {
            return null;
         }

         if (score.getCountingType() == AchievementCountingType.set) {
            count = deltaScore;
         } else if (score.getCountingType() == AchievementCountingType.set_if_higher) {
            if (count <= deltaScore) {
               count = deltaScore;
            }
         } else if (score.getSetValue() != null) {
            if (score.getSetValue() == SetValueType.honor_exp_dec) {
               count += deltaScore;
            } else {
               count = deltaScore;
            }
         } else {
            count += deltaScore;
         }

         boolean canClear = checkCanClearMission(score, count);
         if (e == null) {
            AchievementMissionStatus status = AchievementMissionStatus.NotStart;
            if (canClear) {
               status = AchievementMissionStatus.Complete;
            }

            e = new AchievementEntry(achievementID, subMissionID, status, System.currentTimeMillis(), type.name() + "=" + count);
         } else {
            e.setSubMission(type.name(), String.valueOf(count));
            if (canClear) {
               e.setStatus(AchievementMissionStatus.Complete);
            }
         }

         boolean allClear = false;
         boolean reset = false;
         if (canClear) {
            if (score.getOptionType() != null) {
               switch (score.getOptionType()) {
                  case achievement_complete:
                     allClear = true;
                     break;
                  case achievement_reset:
                     e.setSubMission(type.name() + "=0");
                     e.setStatus(AchievementMissionStatus.NotStart);
                     reset = true;
               }
            }

            int missionCount = mission.getMissions().size();
            if (!reset) {
               if (missionCount <= 1) {
                  allClear = true;
               } else {
                  int checkCount = 0;

                  for (AchievementEntry aex : new ArrayList<>(achievement.getAchievements())) {
                     if (aex.getAchievementID() == achievementID && aex.getStatus() == AchievementMissionStatus.Complete) {
                        checkCount++;
                     }
                  }

                  if (checkCount >= missionCount - 1) {
                     allClear = true;
                  }
               }
            }

            if (allClear) {
               achievement.removeAllAchievement(player, achievementID);
               e.setMission(-1);
               e.setStatus(AchievementMissionStatus.Complete);
            }
         }
      }

      return e;
   }

   public static boolean checkCanClearMission(Score score, long count) {
      if (score.getCountingType() != null) {
         switch (score.getCountingType()) {
            case sum:
            case counting:
               if (score.getTargetScore() <= count) {
                  return true;
               }
               break;
            case set_if_higher:
            case set:
               if (score.getTargetScore() <= count) {
                  return true;
               }
         }
      } else if (score.getTargetScore() <= count) {
         return true;
      }

      return false;
   }
}
