package objects.utils;

import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatFlag;

public class SecondaryStatFlagGenerator {
   public static void main(String[] args) {
      SecondaryStat stat = new SecondaryStat(null);

      for (SecondaryStatFlag flag : SecondaryStatFlag.values()) {
         if (!flag.isIndie()) {
            Object obj = stat.getVarriable(flag.name() + "Value");
            if (obj == null) {
               System.out.println("public int " + flag.name() + "Value = -99999;");
               System.out.println("public int " + flag.name() + "Reason = -99999;");
               System.out.println("public long " + flag.name() + "Till = -99999;");
               System.out.println("public int " + flag.name() + "Level = -99999;");
               System.out.println("public int " + flag.name() + "FromID = -99999;");
               System.out.println();
            }
         }
      }
   }
}
