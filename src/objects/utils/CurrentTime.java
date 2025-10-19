package objects.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CurrentTime {
   public static String getCurrentTime() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
      return simpleTimeFormat.format(calz.getTime());
   }

   public static String getAllCurrentTime1(long times) {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("MM월dd일");
      return simpleTimeFormat.format(times);
   }

   public static String getAllCurrentTime() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      return simpleTimeFormat.format(calz.getTime());
   }

   public static String getCurrentTime2() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("yyyyMMdd");
      return simpleTimeFormat.format(calz.getTime());
   }

   public static int getLeftTimeFromMinute(int minute) {
      Calendar d = Calendar.getInstance(TimeZone.getTimeZone("KST"));
      int min = d.get(12);
      int sec = d.get(13);
      int secs = min * 60 + sec;
      return minute * 60 - secs % (minute * 60);
   }

   public static int hour() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      return calz.getTime().getHours();
   }

   public static int minute() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      return calz.getTime().getMinutes();
   }

   public static int second() {
      Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
      return calz.getTime().getSeconds();
   }
}
