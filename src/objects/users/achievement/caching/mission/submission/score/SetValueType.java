package objects.users.achievement.caching.mission.submission.score;

public enum SetValueType {
   honor_exp_dec,
   meso,
   nc_stat_level,
   nc_stat_exp_today,
   skill_level,
   day_count,
   makingskill_fatigue_inc,
   character_level,
   guild_commitment_inc_point,
   combokill_count,
   spell_trace_enchant_cost;

   public static SetValueType getType(String name) {
      for (SetValueType t : values()) {
         if (t.name().equals(name)) {
            return t;
         }
      }

      return null;
   }
}
