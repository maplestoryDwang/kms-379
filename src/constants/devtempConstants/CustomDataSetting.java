package constants.devtempConstants;

import database.DBConfig;

public class CustomDataSetting {
   public static String path = DBConfig.isGanglim ? "data/Ganglim1" : "data/Jins";
   public static String filename = "Ganglim.xlsx";
}
