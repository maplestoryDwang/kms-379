package objects.users;

import java.util.EnumMap;
import objects.users.stats.SecondaryStatFlag;

public class TemporaryStatTest {
   public static void main(String[] args) {
      new EnumMap<>(SecondaryStatFlag.class);

      for (SecondaryStatFlag stat : SecondaryStatFlag.values()) {
         if (stat.getBit() >= 715) {
            int delta = 14;
            StringBuilder sb = new StringBuilder();
            sb.append("    ");
            sb.append(stat.name());
            sb.append(" (");
            sb.append(stat.getBit() + delta);
            if (stat.isIndie()) {
               sb.append(", ");
               sb.append(stat.isIndie());
            } else if (stat.getDisease() > 0) {
               sb.append(", ");
               sb.append(stat.getDisease());
            }

            sb.append("),");
            System.out.println(sb.toString());
         }
      }
   }
}
