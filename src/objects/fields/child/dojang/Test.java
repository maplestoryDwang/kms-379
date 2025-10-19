package objects.fields.child.dojang;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {
   public static void main(String[] args) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      int currentWeek = cal.get(3);
      int weekYear = cal.getWeekYear();
      System.out.println(currentWeek + " " + weekYear);

      for (int i = 1; i <= 52; i++) {
         cal = Calendar.getInstance();
         cal.set(1, 2020);
         cal.set(3, i);
         cal.set(7, 1);
         SimpleDateFormat sdf = new SimpleDateFormat("+yyyy-MM-dd");
         String v1 = sdf.format(cal.getTime());
         cal = Calendar.getInstance();
         cal.set(1, 2020);
         cal.set(3, i);
         cal.set(7, 7);
         String v2 = sdf.format(cal.getTime());
         System.out.println(v1 + " " + v2);
      }
   }
}
