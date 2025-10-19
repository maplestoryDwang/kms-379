package objects.users.achievement;

public enum AchievementSubMissionType {
   script,
   quest_qrex_change,
   field_leave,
   user_hit,
   mob_kill,
   quest_state_change,
   achievement_state_change,
   ability_change,
   npcshop_buy,
   field_enter,
   nc_stat_level_up,
   nc_stat_exp_up,
   user_lvup,
   combokill_get_marble,
   combokill_increase,
   multikill,
   day_change,
   lottery_result_item,
   makingskill_lvup,
   pickup_reactor_reward_item,
   makingskill_making,
   makingskill_gather,
   makingskill_decomposition,
   makingskill_synthesize,
   dailygift_get_reward,
   master_piece_success,
   makingskill_fatigue_inc,
   item_use,
   guild_attend_check,
   guild_commitment_inc,
   field_attack_obj_use,
   rune_stone_use_result_success,
   suddenmission_complete,
   suddenmission_reward,
   pickup_mob_reward_item,
   pickup_mob_reward_meso,
   starforce_enchant,
   spell_trace_enchant;

   public static AchievementSubMissionType getType(String name) {
      for (AchievementSubMissionType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
