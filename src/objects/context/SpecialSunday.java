package objects.context;

import database.DBConfig;
import objects.utils.Properties;
import objects.utils.Table;

public class SpecialSunday {
   public static final boolean isActive;
   public static final boolean activeRuneEXP;
   public static final boolean activeCombokillEXP;
   public static final boolean activeSpellTrace;
   public static final boolean activeAbility;
   public static final boolean activeStarForceOpO;
   public static final boolean activeStarForce100;
   public static final boolean activeStarForceDiscount;
   public static final boolean activeSoulGacha;
   public static final boolean activeMcUP;
   public static boolean activeCubeUpFever = false;
   public static final String sundayContext;
   public static final String sundayTitle;

   static {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "SpecialSunday.data");
      isActive = table.getProperty("activeSunday", false);
      activeRuneEXP = table.getProperty("activeRuneEXP", false);
      activeCombokillEXP = table.getProperty("activeCombokillEXP", false);
      activeSpellTrace = table.getProperty("activeSpellTrace", false);
      activeAbility = table.getProperty("activeAbility", false);
      activeStarForceOpO = table.getProperty("activeStarForceOpO", false);
      activeStarForce100 = table.getProperty("activeStarForce100", false);
      activeStarForceDiscount = table.getProperty("activeStarForceDiscount", false);
      activeSoulGacha = table.getProperty("activeSoulGacha", false);
      activeMcUP = table.getProperty("activeMcUP", false);
      if (!DBConfig.isGanglim) {
         activeCubeUpFever = table.getProperty("activeCubeUpFever", false);
      }

      sundayContext = table.getProperty("sundayContext", "");
      sundayTitle = table.getProperty("sundayTitle", "");
   }
}
