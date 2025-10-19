package constants;

public class Locales {
   public static String getKoreanJosa(String text, JosaType type) {
      text = text.trim();
      char lastChar = text.charAt(text.length() - 1);
      if (lastChar < '가' || lastChar >= '\ud800') {
         return "";
      } else {
         if ((lastChar - '가') % 28 != 0) {
            switch (type) {
               case 이가:
                  return "이";
               case 은는:
                  return "은";
               case 을를:
                  return "을";
               case 과와:
                  return "과";
            }
         } else {
            switch (type) {
               case 이가:
                  return "가";
               case 은는:
                  return "는";
               case 을를:
                  return "를";
               case 과와:
                  return "와";
            }
         }

         return "";
      }
   }
}
