package network.auction.processors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class asdasd {
   public static void main(String[] args) {
      Date date = new Date(1596089934552L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      CAL.setTime(date);
      String fDate = sdf.format(CAL.getTime());
      System.out.println(fDate);
   }
}
